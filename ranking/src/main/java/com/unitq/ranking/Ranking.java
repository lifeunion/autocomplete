package com.unitq.ranking;

public class Ranking {
    private String words;
    private int rank;

    public Ranking(String words, int rank) {
        this.words = words;
        this.rank = rank;
    }

    public Ranking() {}

    public int getRank() {
        return this.rank;
    }
}

