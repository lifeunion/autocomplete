package com.unitq.autocomplete;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableEurekaClient
@SpringBootApplication
@RestController
@RequestMapping("/autocomplete")
public class AutocompleteApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutocompleteApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	// call suggestion endpoint given one prefix plus variation to get suggestion string
	@RequestMapping("/{prefix}")
	public List<String> getCompletionStringOptions(@PathVariable("prefix") String prefix) {
		List<String> suggestions = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();

		String cleanPrefix = "";
		for (char c: prefix.toLowerCase().toCharArray()) {
			if (Character.isLetter(c) || Character.isDigit(c)) cleanPrefix += c;
		}

		//Keyword suggestionByCompletePrefix = restTemplate.getForObject("http://localhost:8901/preprocessor/suggestion/" + prefix, Keyword.class);
		String suggestionByCompletePrefix = restTemplate.getForObject("http://localhost:8901/preprocessor/suggestion/" + prefix, String.class);
		if (suggestionByCompletePrefix.equals("FALLBACK")) {
			List<String> keysList = new ArrayList<>();
			ResponseEntity<KeywordList> fallback = restTemplate.getForEntity("http://localhost:8080/top-keywords", KeywordList.class);
			for (Keyword k: fallback.getBody().getKeywords()) {
				keysList.add(k.getWords());
			}
			return keysList;
		}

		suggestions.add(suggestionByCompletePrefix);

		if (prefix.length() >=2) {
			Character lastChar = prefix.charAt(prefix.length()-1);
			List<Character> charsList = getSurroundingKeyboardCharsBy(lastChar);
			for (Character c: charsList) {
				/*ResponseEntity<KeywordList> spellChecked = restTemplate.getForObject("http://localhost:8901/preprocessor/suggestion/" +
						prefix.substring(0, prefix.length() - 1) + c, KeywordList.class);*/
				String spellChecked = restTemplate.getForObject("http://localhost:8901/preprocessor/suggestion/" + prefix.substring(0, prefix.length() - 1) + c, String.class);
				if (spellChecked.length() > 2) suggestions.add(spellChecked);
			}
		}

		if (suggestions.isEmpty()) {
			suggestions = getFallbackSuggestions();
		}
		return suggestions;
	}

	private List<String> getFallbackSuggestions() {
		List<String> keysList = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<KeywordList> fallback = restTemplate.getForEntity("http://localhost:8080/top-keywords", KeywordList.class);
		for (Keyword k: fallback.getBody().getKeywords()) {
			keysList.add(k.getWords());
		}
		return keysList;
	}

	private List<Character> getSurroundingKeyboardCharsBy(Character c) {
		switch (c) {
			case 'q':
				return Arrays.asList('w', 'a', '1', '2');
			case 'w':
				return Arrays.asList('q', 'a', 's', 'e', '2', '3');
			case 'e':
				return Arrays.asList('w', 'r', 's', 'd', 'f', '3', '4');
			case 'r':
				return Arrays.asList('e', 't', 'f', '4', '5');
			case 't':
				return Arrays.asList('f', 'g', 'r', 'y', '5', '6');
			case 'y':
				return Arrays.asList('t', 'g', 'h', 'u', '6', '7');
			case 'u':
				return Arrays.asList('y', 'h', 'j', 'i', '7', '8');
			case 'i':
				return Arrays.asList('u', 'j', 'k', 'o', '8', '9');
			case 'o':
				return Arrays.asList('i', 'k', 'l', 'p', '9', '0');
			case 'p':
				return Arrays.asList('o', 'l', '0');
			case 'a':
				return Arrays.asList('q', 'w', 's', 'z');
			case 's':
				return Arrays.asList('a', 'w', 'e', 'd', 'x', 'z');
			case 'd':
				return Arrays.asList('x', 's', 'e', 'r', 'f', 'c');
			case 'f':
				return Arrays.asList('d', 'r', 'c', 'v', 'g');
			case 'g':
				return Arrays.asList('f', 't' ,'v', 'b', 'h');
			case 'h':
				return Arrays.asList('g', 'y', 'b', 'n', 'j');
			case 'j':
				return Arrays.asList('h', 'u', 'n', 'm', 'k');
			case 'k':
				return Arrays.asList('j', 'i', 'm', 'l', 'o');
			case 'l':
				return Arrays.asList('k', 'o', 'p');
			case 'z':
				return Arrays.asList('a', 's', 'x');
			case 'x':
				return Arrays.asList('z', 's', 'd', 'd');
			case 'c':
				return Arrays.asList('x', 'd', 'f', 'v');
			case 'v':
				return Arrays.asList('c', 'f', 'g', 'b');
			case 'b':
				return Arrays.asList('v', 'g', 'h', 'n');
			case 'n':
				return Arrays.asList('b', 'h', 'j', 'm');
			case 'm':
				return Arrays.asList('n', 'j', 'k');
			case '1':
				return Arrays.asList('2', 'q');
			case '2':
				return Arrays.asList('3', 'w', 'q');
			case '3':
				return Arrays.asList('2', '4', 'w', 'e');
			case '4':
				return Arrays.asList('3', '5', 'e', 'r');
			case '5':
				return Arrays.asList('4', '6', 'r', 't');
			case '6':
				return Arrays.asList('5', '7', 't', 'y');
			case '7':
				return Arrays.asList('6', '8', 'y', 'u');
			case '8':
				return Arrays.asList('7', '9', 'u', 'i');
			case '9':
				return Arrays.asList('8', '0', 'i', 'o');
			case '0':
				return Arrays.asList('9', 'o', 'p');
		}
		return new ArrayList<>();
	}
}

