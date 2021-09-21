package com.trippple.models;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class Document {
    private final String corpus;
    private ArrayList<Passage> passages = null;
    public Map<String, Integer> wordCounts;

    public Document(String text) {
        this.corpus = cleanText(text);
        passages = new ArrayList<Passage>();
        for (String pass : corpus.split("\n")) {
            passages.add(new Passage(pass,this));
        }
        this.wordCounts = getWordCounts(passages);
    }

    public Document(ArrayList<Passage> passages) {
        String text = passages.stream()
                .map(passage -> passage.text)
                .collect(Collectors.joining("\n"));
        this.corpus = cleanText(text);
        this.passages = passages;
        for (Passage passage : this.passages) {
            passage.setDocument(this);
        }
        this.wordCounts = getWordCounts(passages);
    }

    private Map<String, Integer> getWordCounts(ArrayList<Passage> passages) {
        var map = new PatriciaTrie<Integer>();
        passages.forEach(passage -> passage.wordCounts.forEach((k, v) -> map.merge(k, v, Integer::sum)));
        return map;
    }

    public ArrayList<Passage> GetPassages() {
        return passages;
    }

    private String cleanText(String text) {
        return text.replace("\r", "");
    }

//    public float getDocumentEvidence(Triple userClaim) {
//        return Ranker.getEvidence(this.corpus, userClaim);
//    }

    public String getCorpus() {
        return corpus;
    }

    @Override
    public String toString() {
        String newLine = System.getProperty("line.separator");
        return String.format("CORPUS LENGTH: %d" + newLine + "PASSAGES: %d" + newLine + newLine,
                corpus.length(), passages.stream().count());
    }

    public int countWord(String word) {
        return wordCounts.getOrDefault(word.toLowerCase(), 0);
    }
}
