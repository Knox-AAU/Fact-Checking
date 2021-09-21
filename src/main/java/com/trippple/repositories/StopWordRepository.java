package com.trippple.repositories;

import com.trippple.datasources.IStopWordDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StopWordRepository {
    private final Logger log;
    private final IStopWordDataSource stopWordDataSource;
    private Set<String> stopWordsCache = null;

    public StopWordRepository(IStopWordDataSource stopWordDataSource) {
        this.log = LoggerFactory.getLogger(getClass());
        this.stopWordDataSource = stopWordDataSource;

        log.debug("Constructor done.");
    }

    public Set<String> getAll() throws IOException {
        if (stopWordsCache == null) {
            stopWordsCache = stopWordDataSource.getAll();
        }
        return stopWordsCache;
    }

    public boolean isStopWord(String word) throws IOException {
        if (word == null) {
            throw new IllegalArgumentException("Cannot check for stopwords on null value.");
        }
        var stopWords = getAll();
        return stopWords.contains(word.toLowerCase());
    }

    public List<String> removeStopWords(Iterable<String> words) throws IOException {
        if (words == null) {
            throw new IllegalArgumentException("Cannot remove stopwords from null value");
        }

        var nonStopWords = new ArrayList<String>();
        for (String word : words) {
            if (!isStopWord(word)) {
                nonStopWords.add(word);
            }
        }

        return nonStopWords;
    }
}
