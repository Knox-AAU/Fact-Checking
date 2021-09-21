package com.trippple.models;

public class FlexWord {
    private final int inflectionForm;
    private final String word;

    public FlexWord(String word, int inflectionForm) {
        this.word = word;
        this.inflectionForm = inflectionForm;
    }

    public String getWord() {
        return word;
    }

    public int getInflectionForm() {
        return inflectionForm;
    }

    @Override
    public String toString() {
        return "FlexWord{" +
                "inflectionForm=" + inflectionForm +
                ", word='" + word + '\'' +
                '}';
    }
}
