package com.unitq.apigateway;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class ApigatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApigatewayApplication.class, args);
	}
}

@Data
class Keyword {
	public Keyword(String words) {
		this.words = words;
		this.matches = new HashMap<String, Ranking>();
		this.rank = new Ranking(words, 0);
	}

	private Ranking rank;

	@NonNull
	private String words;
	private HashMap<String, Ranking> matches;

	public void addMatch(String match, Ranking rank) {
		// need to override it anyway atm
		this.matches.put(match, rank);
	}

	protected HashMap<String, Ranking> getMatches() {
		return this.matches;
	}

	protected String getWords() {
		return this.words;
	}
}

class Ranking {
	private String words;
	private int rank;

	public Ranking(String words, int rank) {
		this.words = words;
		this.rank = rank;
	}

	public int getRank() {
		return this.rank;
	}
}

@FeignClient("preprocessor-service")
interface KeywordClient {
	@GetMapping("/keywords")
	CollectionModel<Keyword> readKeywords();
}

@RestController
class TopSearchController {
	private final KeywordClient keywordClient;

	public TopSearchController(KeywordClient keywordClient) {
		this.keywordClient = keywordClient;
	}

	private Collection<Keyword> fallback() {
		return new ArrayList<>();
	}

	@GetMapping("/top-keywords")
	@HystrixCommand(fallbackMethod = "fallback")
	public Collection<Keyword> topKeyword() {
		return keywordClient.readKeywords().getContent().stream().limit(3).collect(Collectors.toList());
	}
}