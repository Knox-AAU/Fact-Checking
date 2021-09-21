package com.trippple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.*;

public class Benchmark {
    private final Logger log;

    public Benchmark() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    public void run(List<List<String>> claims, IPassageRetriever passageRetriever, File outputFile) throws Exception {
        if (claims == null) {
            throw new Exception("Claims cannot be null. Remember to call setClaims on benchmark class");
        }
        if (outputFile == null) {
            throw new Exception("outputfile cannot be null");
        }

        if (outputFile.createNewFile()) {
            log.debug("Created benchmark output file because it didn't exist.");
        }

        StopWatch stopWatch = new StopWatch();

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)))) {

            stopWatch.start();
            for (int i = 0; i < claims.size(); i++) {
                var claim = claims.get(i);
                var mostRelevantPassages = passageRetriever.getPassages(claim.toArray(String[]::new), 5);

                writer.write(String.format("CLAIM: %s\n", claim));
                for (var relevantPassage : mostRelevantPassages) {
                    writer.write(String.format("RELEVANT PASSAGE: %s\n", relevantPassage.getPassage().getText()));
                }
                writer.write("------\n");

                log.debug(String.format("Passage found for claim %d of %d", i + 1, claims.size()));
            }
            stopWatch.stop();

            writer.write(String.format("RUNTIME: %.4f seconds\n", stopWatch.getTotalTimeSeconds()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
