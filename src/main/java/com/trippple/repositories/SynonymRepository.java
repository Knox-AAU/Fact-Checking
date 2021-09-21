package com.trippple.repositories;

import com.trippple.datasources.ISynonymSource;

import java.util.List;

public class SynonymRepository {
    public ISynonymSource synonymSource;

    public SynonymRepository(ISynonymSource synonymSource) {
        this.synonymSource = synonymSource;
    }

    public List<String> getSynonyms(String word) {
        return synonymSource.getSynonyms(word);
    }
}
