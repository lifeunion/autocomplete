package com.unitq.txtfileprocessor;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@EnableEurekaClient
@SpringBootApplication
@RestController
@RequestMapping("/preprocessor")
public class PreprocessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PreprocessorApplication.class, args);
	}
	private String fileName = "topkeywords.txt";
	private List<Keyword> dictionary = new ArrayList<>();
	private HashMap<String, Set<String>> index = new HashMap<>();

	@Bean
	ApplicationRunner init() {
		return args -> {
			try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
				stream.forEach(word -> {
					dictionary.add(new Keyword(word));
				});
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		};
	}

	@RequestMapping("/keywords")
	public List<Keyword> getKeywords() {
		return dictionary;
	}

	@RequestMapping("/keywords/{prefix}")
	private List<Keyword> getWordsStartingWith(@PathVariable("prefix") String prefix) {
		List<Keyword> answer = new ArrayList<>();
		Keyword[] keys = new Keyword[dictionary.size()];
		for (int i=0; i < keys.length; i++) keys[i] = dictionary.get(i);

		Arrays.sort(keys, new Comparator<Keyword>() {
			@Override
			public int compare(Keyword o1, Keyword o2) {
				return o1.getWords().compareTo(o2.getWords());
			}
		});

		int lo = 0;
		int high = keys.length-1;
		int lowRangeIndex = Integer.MIN_VALUE;
		int highRangeIndex = Integer.MAX_VALUE;

		while (lo < high) {
			int mid = (lo+high)/2;
			System.out.println("key" + keys[mid].getWords());
			System.out.println("prefix" + prefix);
			System.out.println(keys[mid].getWords().substring(0, prefix.length()).compareTo(prefix));
			if (keys[mid].getWords().substring(0, prefix.length()).compareTo(prefix) < 0) {
				lo = mid + 1;
				if (mid > lowRangeIndex && keys[mid].getWords().startsWith(prefix)) lowRangeIndex = mid;
			}
			else if (keys[mid].getWords().substring(0, prefix.length()).compareTo(prefix) > 0)  {
				high= mid-1;
				if (mid < highRangeIndex && keys[mid].getWords().startsWith(prefix)) highRangeIndex = mid;
			} else if (keys[mid].getWords().substring(0, prefix.length()).compareTo(prefix) == 0) {
				lowRangeIndex = mid-2;
				highRangeIndex = mid+2;
			}
			lo++;
		}

		if (lowRangeIndex >=0 && highRangeIndex <=keys.length-1) {
			for (int i = lowRangeIndex; i <= highRangeIndex; i++) {
				answer.add(keys[i]);
			}
		} else {
			for (int i = lo; i <= high; i++) {
				answer.add(keys[i]);
			}
		}
		return answer;
	}

	private List<Keyword> findByStartingPrefix(String prefix) {
		List<Keyword> answer = new ArrayList<>();
		List<Keyword> possibleMatches = getWordsStartingWith(prefix);
		Keyword currKeyword = new Keyword(prefix);
		RestTemplate restTemplate = new RestTemplate();

		// reorder based on rank
		for(int i=0; i < possibleMatches.size(); i++) {
			String currWord = possibleMatches.get(i).getWords();
			Ranking currRank = restTemplate.getForObject("http://localhost:8803/ranking/" + currWord + "/" + prefix, Ranking.class);
			currKeyword.addMatch(currWord, currRank);
		}

		Map<String, Ranking> sortedByRank = currKeyword.getMatches();
		List<Map.Entry<String, Ranking>> toSort = new ArrayList<>(sortedByRank.entrySet());

		Collections.sort(toSort, new Comparator<Map.Entry<String, Ranking>>() {
			@Override
			public int compare(Map.Entry<String, Ranking> o1, Map.Entry<String, Ranking> o2) {
				if (o1.getValue().getRank() > o2.getValue().getRank()) return -1;
				else if (o1.getValue().getRank() < o2.getValue().getRank()) return 1;
				else return 0;
			}
		});

		for (Map.Entry<String, Ranking> entry: toSort) {
			answer.add(new Keyword(entry.getKey()));
		}
		return answer;
	}

	@RequestMapping("/suggestion/{prefix}")
	public List<String> getSuggestion(@PathVariable("prefix") String prefix) {
		List<String> stringList = new ArrayList<>();
		for (Keyword k: getBestMatchWord(prefix)) {
			stringList.add(k.getWords());
		}
		return stringList;
	}

	@RequestMapping("/spellcheck/{prefix}")
	public List<Keyword> getSpellCheckSuggestion(@PathVariable("prefix") String prefix) {
		return getBestMatchWord(prefix);
	}

	private List<Keyword> getBestMatchWord(String prefix) {
		if (prefix.length() == 0 || prefix == null) return new ArrayList<>();
		if (prefix.length() == 1) return findByStartingPrefix(prefix);
		return findByStartingPrefix(prefix);
	}
}