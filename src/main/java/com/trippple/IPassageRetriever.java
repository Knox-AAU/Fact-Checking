package com.trippple;

import java.util.ArrayList;
import java.util.List;

public interface IPassageRetriever {
    IPassageRetrieverResult getPassage(String[] claim);
    List<IPassageRetrieverResult> getPassages(String[] claim, int maxPassages);

    interface IPassageRetrieverResult {
        double getRank();
        IPassage getPassage();
        IDocument getDocument();
    }

    interface IWord {
        long countWord(String word);
        long getWordCount();
    }

    interface IPassage extends IWord {
        String getText();
    }

    interface IDocument extends IWord {
        ArrayList<? extends IPassage> getPassages();
        boolean hasPassages();
        String getTitle();
    }

    interface ICollection extends IWord {
        long getVocabularySize();
        ArrayList<? extends IDocument> getDocuments();
        boolean hasDocuments();
    }
}
