package com.trippple.processing;

import com.trippple.repositories.SynonymRepository;

public class Synonym {
    private final SynonymRepository synonymRepository;
    public Synonym(SynonymRepository synonymRepository) {
        this.synonymRepository = synonymRepository;
    }

    public String[] getSynonyms(String word) {
        return synonymRepository.getSynonyms(word).toArray(String[]::new);
    }
}
