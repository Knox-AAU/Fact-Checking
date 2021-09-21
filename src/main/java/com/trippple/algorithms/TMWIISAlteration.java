package com.trippple.algorithms;

import com.trippple.IPassageRetriever;

public class TMWIISAlteration extends TMWIIS{

    @Override
    public double computePassageRank(
            String[] wordSet,
            IPassageRetriever.IPassage passage,
            IPassageRetriever.IDocument document,
            IPassageRetriever.ICollection collection,
            double[] lambdas) {
        double passageRank = 1;
        for (String word : wordSet) {
            passageRank *= (((float) passage.countWord(word)) / (((float) document.countWord(word)) + ((float) collection.countWord(word)) + 1)) *
                    computeProbabilityOfWordGivenPassage(
                            computePassageLevelEvidence(passage.countWord(word), passage.getWordCount(), collection.getVocabularySize()),
                            computeDocumentLevelEvidence(document.countWord(word), document.getWordCount(), collection.getVocabularySize()),
                            computeCollectionLevelEvidence(collection.countWord(word), collection.getVocabularySize())
                    );
        }
        return passageRank;
    }
}
