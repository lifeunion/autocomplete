package com.unitq.ranking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableEurekaClient
@SpringBootApplication
@RestController
@RequestMapping("/ranking")
public class RankingResource {
	public static void main(String[] args) {
		SpringApplication.run(RankingResource.class, args);
	}

	private HashMap<String, Map<String, Integer>> ranking = new HashMap<>();

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@RequestMapping("/{word}/{prefix}")
	public Ranking getRanking(@PathVariable("prefix") String prefix, @PathVariable("word") String word) {
		RestTemplate restTemplate = new RestTemplate();
		//KeywordList dictionary = restTemplate.getForObject("http://localhost:8901/preprocessor/keywords", KeywordList.class);

		if (prefix == null || word == null)  {
			System.out.println("Ranking service return without data entered yet.");
			return new Ranking();
		}

		assignRanking(prefix, word);
		return ranking.get(prefix).get(word) == null ? new Ranking(word, 0): new Ranking(word, ranking.get(prefix).get(word));
	}

	private void assignRanking(String prefix, String word) {
		if (prefix == null || word == null || word.isEmpty() || prefix.isEmpty()) return;

		if (ranking.get(prefix) == null) {
			ranking.put(prefix, new HashMap<>());
		}

		if(ranking.get(prefix).get(word) == null) {
			ranking.get(prefix).put(word, 0);
		}
		ranking.get(prefix).put(word, ranking.get(prefix).get(word) + 1);
	}
}
