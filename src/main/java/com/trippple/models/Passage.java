package com.trippple.models;

import com.trippple.utils.StringUtils;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.Map;

public class Passage {
    public String text;
    public String[] words;
    public String[] lowerWords;
    public Document document;
    public Map<String, Integer> wordCounts;

    public Passage(String text, Document document) {
        this.text = text;
        this.words = getWords(text);
        this.lowerWords = getWords(text.toLowerCase());
        this.document = document;
        this.wordCounts = getWordCounts(this.lowerWords);
    }

    public Passage(String text) {
        this.text = text;
        this.words = getWords(text);
        this.lowerWords = getWords(text.toLowerCase());
        this.document = null;
        this.wordCounts = getWordCounts(this.lowerWords);
    }

    public void setDocument(Document doc) {
        document = doc;
    }

    public int countWord(String word) {
        return wordCounts.getOrDefault(word.toLowerCase(), 0);
    }

    private Map<String, Integer> getWordCounts(String[] words) {
        var map = new PatriciaTrie<Integer>();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (map.containsKey(word)) {
                map.computeIfPresent(word, (s, integer) -> integer + 1);
            } else {
                map.put(word, 1);
            }
        }
        return map;
    }

    private String[] getWords(String text) {
        return StringUtils.splitStringToArray(text);
    }

    @Override
    public String toString() {
        return text;
    }
}

