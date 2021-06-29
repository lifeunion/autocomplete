package com.unitq.ranking;

import lombok.NonNull;

import java.util.HashMap;

public class Keyword {
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

