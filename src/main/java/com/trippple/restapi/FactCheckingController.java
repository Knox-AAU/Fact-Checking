package com.trippple.restapi;

import com.trippple.IPassageRetriever;
import com.trippple.processing.NordjyskeDatasetProcessing;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FactCheckingController {
    private final IPassageRetriever passageRetriever;

    FactCheckingController(IPassageRetriever evidenceSource) {
        this.passageRetriever = evidenceSource;
    }

    @GetMapping("/factcheck")
    List<Result> returnResults(@RequestParam String object,
                               @RequestParam String predicate,
                               @RequestParam String subject) {
        var claim = new String[]{object, predicate, subject};
        return passageRetriever.getPassages(claim, 5).stream()
                .map(corroboration -> {
                    var sourceDocument = "Unknown";
                    var document = corroboration.getDocument();
                    if (document instanceof NordjyskeDatasetProcessing.Document) {
                        sourceDocument = ((NordjyskeDatasetProcessing.Document) document).getSourceFile();
                    }
                    return new Result(
                            corroboration.getRank(),
                            corroboration.getPassage().getText(),
                            corroboration.getDocument().getTitle(),
                            sourceDocument
                    );
                }).collect(Collectors.toList());
    }

    @GetMapping("/test")
    String returnPassage(@RequestParam String object,
                         @RequestParam String predicate,
                         @RequestParam String subject) {
        return "Hello World";
    }

    public static class Result implements Comparable<Result> {
        public double score;
        public String passage;
        public String documentTitle;
        public String sourceDocument;

        public Result(double score, String passage, String documentTitle, String sourceDocument) {
            this.score = score;
            this.passage = passage;
            this.documentTitle = documentTitle;
            this.sourceDocument = sourceDocument;
        }

        @Override
        public String toString() {
            return String.format("Score: %f%nPassage: %s%nTitle of source document: %s",
                    score, passage, documentTitle);
        }

        @Override
        public int compareTo(Result o) {
            return Double.compare(this.score, o.score);
        }
    }
}