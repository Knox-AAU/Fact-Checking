package com.trippple.processing;

import com.trippple.models.FlexWord;
import com.trippple.models.Flexion;
import com.trippple.repositories.FlexionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stemmer {
    private final FlexionRepository flexionRepository;
    private final Logger log;

    public Stemmer(FlexionRepository flexionRepository) {
        this.flexionRepository = flexionRepository;
        log = LoggerFactory.getLogger(this.getClass());
    }

    public String stem(String givenWord) {
        log.debug("Stemming word "+givenWord);
        Flexion flexion = flexionRepository.getFlexionForWord(givenWord);
        if (flexion == null) {
           return givenWord;
        }
        FlexWord word = flexion.getStem();
        var stemmedWord = word.getWord();
        log.debug("Found stem ("+stemmedWord+") for word "+givenWord);
        return stemmedWord;
    }

    public String[] stemWords(String[] words) {
        var stemmedWords = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            stemmedWords[i] = stem(words[i]);
        }
        return stemmedWords;
    }
}