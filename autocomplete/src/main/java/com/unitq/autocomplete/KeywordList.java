package com.unitq.autocomplete;

import java.util.ArrayList;
import java.util.List;

public class KeywordList {
    private List<Keyword> keywords;

    public KeywordList() {
        keywords = new ArrayList<>();
    }

    public List<Keyword> getKeywords() {
        List<Keyword> keywordList = new ArrayList<>();
        for (Keyword k: keywords) {
            keywordList.add(k);
        }
        return keywordList;
    }
}

