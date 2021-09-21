package com.trippple.algorithms;

import com.trippple.IPassageRetriever;

import java.util.ArrayList;
import java.util.Comparator;

public class TMWIIS {
    public double[] lambdas;

    public TMWIIS() {
        this.setLambdas();
    }

    public void setLambdas() {
        lambdas = new double[]{0.4f, 0.4f, 0.2f};
    }

    public Result computeMostRelevantPassage(
            String[] claim,
            IPassageRetriever.ICollection collection
    ) {
        if (!collection.hasDocuments()) {
            return null;
        }

        Result currentBest = null;
        for (int i = 0; i < collection.getDocuments().size(); i++) {
            IPassageRetriever.IDocument doc = collection.getDocuments().get(i);
            if (!doc.hasPassages())
                continue;
            for (int j = 0; j < doc.getPassages().size(); j++) {
                IPassageRetriever.IPassage passage = doc.getPassages().get(j);
                double passageRank = computePassageRank(claim, passage, doc, collection, lambdas);
                if (currentBest == null || currentBest.getRank() < passageRank) {
                    currentBest = new Result(passage, doc, passageRank);
                }
            }
        }
        return currentBest;
    }

    public ArrayList<Result> computeMostRelevantPassages(
            String[] claim,
            IPassageRetriever.ICollection collection,
            int maxPassages
    ) {
        if (!collection.hasDocuments()) {
            return null;
        }

        var passages = new ArrayList<Result>();
        for (int i = 0; i < collection.getDocuments().size(); i++) {
            IPassageRetriever.IDocument doc = collection.getDocuments().get(i);
            if (!doc.hasPassages())
                continue;
            for (int j = 0; j < doc.getPassages().size(); j++) {
                IPassageRetriever.IPassage passage = doc.getPassages().get(j);
                double passageRank = computePassageRank(claim, passage, doc, collection, lambdas);
                passages.add(new Result(passage, doc, passageRank));
            }
        }

        /*
         * Sort list by score in descending order and take the first 5 passages
         */
        passages.sort(Comparator.comparingDouble(Result::getRank).reversed());
        if (passages.size() > maxPassages && maxPassages != -1) {
            /* Remove everything else than the first 5 entries */
            passages.subList(maxPassages, passages.size()).clear();
        }

        return passages;
    }

    public double computePassageRank(
            String[] wordSet,
            IPassageRetriever.IPassage passage,
            IPassageRetriever.IDocument document,
            IPassageRetriever.ICollection collection,
            double[] lambdas) {
        double passageRank = 1;
        for (String word : wordSet) {
            passageRank *= computeProbabilityOfWordGivenPassage(
                    computePassageLevelEvidence(passage.countWord(word), passage.getWordCount(), collection.getVocabularySize()),
                    computeDocumentLevelEvidence(document.countWord(word), document.getWordCount(), collection.getVocabularySize()),
                    computeCollectionLevelEvidence(collection.countWord(word), collection.getVocabularySize())
            );
        }
        return passageRank;
    }
    public double computeProbabilityOfWordGivenPassage(
            double passageLevelEvidence,
            double documentLevelEvidence,
            double collectionLevelEvidence
    ) {
        return lambdas[0] * passageLevelEvidence + lambdas[1] * documentLevelEvidence + lambdas[2] * collectionLevelEvidence;
    }
    public double computePassageLevelEvidence(double wordInPassageCount, double passageWordCount, double vocabularySize) {
        return (wordInPassageCount + 1) / (passageWordCount + vocabularySize);
    }
    public double computeDocumentLevelEvidence(double wordInDocumentCount, double documentWordCount, double vocabularySize) {
        return (wordInDocumentCount + 1) / (documentWordCount + vocabularySize);
    }
    public double computeCollectionLevelEvidence(double wordInCollectionCount, double vocabularySize) {
        return wordInCollectionCount / vocabularySize;
    }

    public static class Result {
        private final IPassageRetriever.IPassage passage;
        private final IPassageRetriever.IDocument document;
        private final double rank;
        Result(IPassageRetriever.IPassage passage, IPassageRetriever.IDocument document, double rank) {
            this.passage = passage;
            this.document = document;
            this.rank = rank;
        }
        public IPassageRetriever.IPassage getPassage() {
            return this.passage;
        }
        public double getRank() {
            return this.rank;
        }
        public IPassageRetriever.IDocument getDocument(){
            return this.document;
        }
    }
}
