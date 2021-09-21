package com.trippple.models;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Flexion implements Comparable<Flexion> {
    private final List<FlexWord> words;
    private final WordClass wordClass;

    public Flexion(Map<String, Integer> wordDictionary, WordClass wordClass) {
        List<FlexWord> words = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : wordDictionary.entrySet()) {
            words.add(new FlexWord(entry.getKey(), entry.getValue()));
        }
        this.words = words;
        this.wordClass = wordClass;
    }

    public Flexion(List<FlexWord> words, WordClass wordClass) {
        this.words = words;
        this.wordClass = wordClass;
    }

    public WordClass getWordClass() {
        return wordClass;
    }

    public FlexWord getStem() {
        return words.get(0);
    }

    public boolean isInflexion(String word) {
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY); // ignore differences such as lower/uppercase and e/é
        for (FlexWord w : words) {
            if (collator.equals(w.getWord(), word)) return true;
        }
        return false;
    }

    public List<FlexWord> getWords() {
        return words;
    }

    @Override
    public String toString() {
        return "Flexion{" +
                "words=" + words +
                ", wordClass=" + wordClass +
                '}';
    }

    @Override
    public int compareTo(Flexion o) {
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY); // ignore differences such as lower/uppercase and e/é

        List<FlexWord> list1 = getWords();
        List<FlexWord> list2 = o.getWords();
        for (FlexWord fword1 : list1) {
            for (FlexWord fword2 : list2) {
                if (collator.equals(fword1.getWord(), fword2.getWord())) {
                    return 0;
                }
            }
        }

        return collator.compare(getStem().getWord(), o.getStem().getWord());
    }
}
