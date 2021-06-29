package com.unitq.suggestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableEurekaClient
@SpringBootApplication
public class SuggestionApplication {
	private Set<String> dictionary = new TreeSet();
	private HashMap<String, Set<String>> index = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(SuggestionApplication.class, args);
	}

	//get data source for prefix
	//increase rank for every get suggestion (not spell check one) in ranking service

	//TODO:
	// add more todos
	// add documentation
	// setup rest template connections
	// setup webclient connections

	/*
	@Query("FROM Keywords k WHERE k.words LIKE: words%")
	private List<String> findByStartingChar(@Param("words") String firstChar);

	@Query("FROM Keywords k WHERE k.words LIKE: words%")
	private List<String> findByStartingPrefix(@Param("words")String prefix);
	*/

	public List<String> getAllWordsFromDb() {
		return session.create
	}

	 */
	// next lexicographic return string is okay for now?
	public String getSuggestion(String prefix) {
		return getNextLexicographicWord(prefix);
	}

	public String getSpellCheckSuggestion(String prefix) {
		return getNextLexicographicWord(prefix);
	}

	private String getNextLexicographicWord(String prefix) {
		if (prefix.length() == 0 || prefix == null) return "FALLBACK";
		if (prefix.length() == 1) return findByStartingChar(prefix).get(0);

		Stream<String> stream = findByStartingPrefix(prefix).stream();
		Set<String> set = stream.limit(1).collect(Collectors.toCollection(TreeSet::new));

		return set.toString();
	}

	/*
		how to do candidate generation?
		- get the prefix
		- loop through chars
		- attempt to spell check?
		- spawn another service for ranking (simplest idea rn is to produce rank based on how many chars matches so far)
		- everytime get service is called, increase the rank
		- higher the rank the better
		- if missing one char, update with closest match, how about keyboard surrounding?
		- how about storing with dicts by prefix alphabet (36 dicts)
		- not equal number of suggestions everytime
		- need to do with rest template first
		- change to webclient afterwards
	*/



}
