package com.trippple;

import com.trippple.processing.Stemmer;
import com.trippple.processing.Synonym;
import com.trippple.repositories.StopWordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.trippple.utils.StringUtils.splitByCapital;

public final class Application {
    private final Logger log;
    private final IPassageRetriever passageRetriever;
    private final Stemmer stemmer;
    private final Benchmark benchmark;
    private final Synonym synonym;
    private final StopWordRepository stopWordRepository;


    public Application(
            IPassageRetriever passageRetriever,
            Stemmer stemmer,
            Benchmark benchmark,
            Synonym synonym,
            StopWordRepository stopWordRepository
    ) {
        this.passageRetriever = passageRetriever;
        this.log = LoggerFactory.getLogger(this.getClass());
        this.stemmer = stemmer;
        this.benchmark = benchmark;
        this.synonym = synonym;
        this.stopWordRepository = stopWordRepository;
    }

    public void main() throws Exception {
        var config = Config.loadConfiguration();
        var claimFilePath = config.getProperty("claimFilePath");
        File benchmarkOutputFile = new File(config.getProperty("benchmarkOutputFilePath"));


        /*
         * Load claims
         */
        log.debug("Loading claims");
        List<List<String>> claims = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(claimFilePath))) {
            while (scanner.hasNextLine())
                claims.add(getClaimFromLine(scanner.nextLine()));
        }

        if (passageRetriever instanceof TMWIISPassageRetriever) {
            ((TMWIISPassageRetriever) passageRetriever).init();
        }

        log.debug("Processing claims");
        var processedClaims = claims.stream()
                .map(claim -> Arrays.asList(generateWordSet(claim.toArray(String[]::new))))
                .collect(Collectors.toList());
        benchmark.run(processedClaims, passageRetriever, benchmarkOutputFile);
    }

    public List<String> getClaimFromLine(String line) {
        List<String> claim = new ArrayList<>();

        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");

            while (rowScanner.hasNext())
                claim.add(rowScanner.next().trim());
        }

        return claim;
    }

    public String[] generateWordSet(String[] claim) {
        return Arrays.stream(claim)
                .flatMap(this::streamSplitByCapital)
                .map(String::toLowerCase)
                .map(stemmer::stem)
                .flatMap(this::streamAddSynonyms)
                .filter(this::filterRemoveStopWords)
                .toArray(String[]::new);
    }

    private Stream<String> streamSplitByCapital(String sentence) {
        var words = splitByCapital(sentence);
        return Arrays.stream(words);
    }

    private Stream<String> streamAddSynonyms(String word) {
        var synonyms = stemmer.stemWords(synonym.getSynonyms(word));
        var result = new String[synonyms.length + 1];
        System.arraycopy(synonyms, 0, result, 0, synonyms.length);
        result[synonyms.length] = word;
        return Arrays.stream(result);
    }

    private boolean filterRemoveStopWords(String word) {
        try {
            return !stopWordRepository.isStopWord(word);
        } catch (IOException e) {
            return true;
        }
    }
}
