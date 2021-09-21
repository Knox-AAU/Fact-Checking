package com.trippple;

import com.trippple.algorithms.TMWIIS;
import com.trippple.processing.NordjyskeDatasetProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TMWIISPassageRetriever implements IPassageRetriever {
    private final Logger log;
    private final TMWIIS algo;
    private final File inputDir;
    private final String outputDirPath;
    private final NordjyskeDatasetProcessing processor;
    private NordjyskeDatasetProcessing.Collection collection;

    public TMWIISPassageRetriever(TMWIIS algo, NordjyskeDatasetProcessing processor) throws IOException {
        this.log = LoggerFactory.getLogger(getClass());
        this.algo = algo;
        this.processor = processor;
        var config = Config.loadConfiguration();
        this.inputDir = new File(config.getProperty("datasetDirectoryPath"));
        this.outputDirPath = config.getProperty("processedDatasetDirectoryPath");
    }

    public void init() throws IOException {
        /*
         * Process json data files
         */
        log.debug("Processing json files");
        processor.process(inputDir.toURI().getPath(), outputDirPath);


        /*
         * Load processed collection
         */
        log.debug("Loading collection");
        collection = processor.loadCollection(outputDirPath + "/collection");
    }

    @Override
    public IPassageRetrieverResult getPassage(String[] claim) {
        return getPassages(claim, 1).get(0);
    }

    @Override
    public List<IPassageRetrieverResult> getPassages(String[] claim, int maxPassages) {
        int fileCount = Objects.requireNonNull(inputDir.listFiles()).length;
        try {
            var passages = processor.runInChunks(
                    algo,
                    claim,
                    collection, 10, fileCount, outputDirPath, maxPassages);
            var results = new ArrayList<IPassageRetrieverResult>();
            for (TMWIIS.Result passage : passages) {
                results.add(new Result(passage.getPassage(), passage.getRank(), passage.getDocument()));
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static class Result implements IPassageRetrieverResult {
        private final double rank;
        private final IPassage passage;
        private final IDocument document;

        public Result(IPassage passage, double rank, IDocument document) {
            this.rank = rank;
            this.passage = passage;
            this.document = document;
        }

        @Override
        public double getRank() {
            return rank;
        }

        @Override
        public IPassage getPassage() {
            return passage;
        }

        @Override
        public IDocument getDocument() {
            return document;
        }
    }
}
