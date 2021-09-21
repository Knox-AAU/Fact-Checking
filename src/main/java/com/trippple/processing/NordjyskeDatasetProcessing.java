package com.trippple.processing;

import com.google.gson.Gson;
import com.trippple.IPassageRetriever;
import com.trippple.algorithms.TMWIIS;
import com.trippple.protobuf.NordjyskeDatasetProtos;
import com.trippple.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class NordjyskeDatasetProcessing {
    private final Logger log;
    private final Stemmer stemmer;
    public NordjyskeDatasetProcessing(Stemmer stemmer) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.stemmer = stemmer;
    }

    public void process(String inputDir, String outputDir) throws IOException {
        File folder = new File(inputDir);
        File[] listOfFiles = folder.listFiles();

        int i = 0;

        var documentCount = 0;
        var collectionBuilder = NordjyskeDatasetProtos.Collection.newBuilder();
        var collectionMap = new HashMap<String, Integer>();

        for (File file : listOfFiles) {
            if (!file.isFile()) continue;

            log.debug("Processing file "+ i++);

            Wrapper wrapper = parseJsonFile(file);
            documentCount += (long) wrapper.content.articles.size();
            var fileBuilder = NordjyskeDatasetProtos.File.newBuilder();
            fileBuilder.setFilename(file.getName());
            for (int a = 0; a < (long) wrapper.content.articles.size(); a++) {
                var documentBuilder = NordjyskeDatasetProtos.Document.newBuilder();
                documentBuilder.setTitle(wrapper.content.articles.get(a).headline);
                var passagesCount = (long) wrapper.content.articles.get(a).paragraphs.size();
                documentBuilder.setPassageCount(passagesCount);
                var wordList = new HashMap<String, Integer>();
                for (int p = 0; p < passagesCount; p++) {
                    var paragraph = wrapper.content.articles.get(a).paragraphs.get(p);
                    if (paragraph.value == null || paragraph.value.length() == 0) continue;
                    var passageMap = new HashMap<String, Integer>();
                    for (String word : StringUtils.splitStringToArray(paragraph.value)) {
                        var processedWord = stemmer.stem(word.toLowerCase());
                        var appearenceCount = wordList.getOrDefault(processedWord, 0);
                        wordList.put(processedWord, appearenceCount + 1);

                        var collectionCount = collectionMap.getOrDefault(processedWord, 0);
                        collectionMap.put(processedWord, collectionCount + 1);

                        var passageCount = passageMap.getOrDefault(processedWord, 0);
                        passageMap.put(processedWord, passageCount + 1);
                    }
                    var passageBuilder = NordjyskeDatasetProtos.Passage.newBuilder();
                    for (Map.Entry<String, Integer> entry : passageMap.entrySet()) {
                        var word = NordjyskeDatasetProtos.Word.newBuilder();
                        word.setAppearanceCount(entry.getValue());
                        word.setWord(entry.getKey());
                        passageBuilder.addWords(word);
                    }
                    passageBuilder.setText(paragraph.value);
                    documentBuilder.addPassages(passageBuilder);
                }
                for (Map.Entry<String, Integer> entry : wordList.entrySet()) {
                    var word = NordjyskeDatasetProtos.Word.newBuilder();
                    word.setAppearanceCount(entry.getValue());
                    word.setWord(entry.getKey());
                    documentBuilder.addWords(word);
                }
                fileBuilder.addDocuments(documentBuilder);
            }
            fileBuilder.build().writeTo(new FileOutputStream(outputDir + "/" + file.getName() + "-index"));
        }

        for (Map.Entry<String, Integer> entry : collectionMap.entrySet()) {
            var word = NordjyskeDatasetProtos.Word.newBuilder();
            word.setAppearanceCount(entry.getValue());
            word.setWord(entry.getKey());
            collectionBuilder.addWords(word);
        }
        collectionBuilder.setDocumentCount(documentCount);
        collectionBuilder.build().writeTo(new FileOutputStream(outputDir + "/collection"));
    }

    public HashMap<String, Integer> computeCollectionIndex(String inputDir) throws IOException {
        File folder = new File(inputDir);
        File[] listOfFiles = folder.listFiles();
        var map = new HashMap<String, Integer>();
        for (File file : listOfFiles) {
            if (!file.isFile() || !file.getName().endsWith("-index")) continue;
            var protoFile = NordjyskeDatasetProtos.File.parseFrom(new FileInputStream(file));
            for (var doc : protoFile.getDocumentsList()) {
                for (var word : doc.getWordsList()) {
                    if (map.containsKey(word.getWord())) {
                        map.computeIfPresent(word.getWord(), (s, integer) -> integer + word.getAppearanceCount());
                    } else {
                        map.put(word.getWord(), word.getAppearanceCount());
                    }
                }
            }
        }
        return map;
    }

    public Collection loadCollection(String filename) throws IOException {
        return new Collection(loadCollectionWordList(filename));
    }

    public ArrayList<TMWIIS.Result> runInChunks(TMWIIS algo, String[] claim, Collection collection, int chunkSize, int fileCount, String inputDir, int maxPassages) throws IOException {
        int chunkCount = fileCount / chunkSize;
        ArrayList<TMWIIS.Result> res = new ArrayList<>();
        for (int i = 0; i <= chunkCount; i++) {
            int from = i * chunkSize;
            collection.setDocuments(loadDocumentSubset(inputDir, from, chunkSize));
            var result = algo.computeMostRelevantPassages(claim, collection, maxPassages);
            if (result != null) {
                res.addAll(result);
            }
        }

        /*
         * Filter out duplicate passages
         */
        res = (ArrayList<TMWIIS.Result>) res.stream().filter(distinctByKey(TMWIIS.Result::getRank)).distinct().collect(Collectors.toList());

        res.sort(Comparator.comparingDouble(TMWIIS.Result::getRank).reversed());
        if (res.size() > maxPassages && maxPassages != -1)
            res.subList(maxPassages, res.size()).clear();

        return res;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public ArrayList<Document> loadDocumentSubset(String inputDir, int from, int count) throws IOException {
        log.debug("Loading jsonfile subset "+from+" to "+(count+from));
        var docs = new ArrayList<Document>();
        File folder = new File(inputDir);
        File[] listOfFiles = Objects.requireNonNull(folder.listFiles());
        for (int i = 0; i < count; i++) {
            if (listOfFiles.length <= from + i) {
                break;
            }
            File file = listOfFiles[from + i];
            if (file.getName().endsWith("-index")) {
                docs.addAll(loadDocsFromFile(file.getAbsolutePath()));
            }
        }
        return docs;
    }

    public ArrayList<Document> loadDocuments(String inputDir) throws IOException {
        var docs = new ArrayList<Document>();
        File folder = new File(inputDir);
        File[] listOfFiles = Objects.requireNonNull(folder.listFiles());
        for (File file : Objects.requireNonNull(listOfFiles)) {
            if (file.getName().endsWith("-index")) {
                docs.addAll(loadDocsFromFile(file.getAbsolutePath()));
            }
        }
        return docs;
    }

    public ArrayList<Document> loadDocsFromFile(String filePath) throws IOException {
        var docs = new ArrayList<Document>();
        var protoFile = NordjyskeDatasetProtos.File.parseFrom(new FileInputStream(filePath));
        var protoDocs = protoFile.getDocumentsList();
        for (var protoDoc : protoDocs) {
            var map = new HashMap<String, Integer>();
            for (NordjyskeDatasetProtos.Word word : protoDoc.getWordsList()) {
                map.put(word.getWord(), word.getAppearanceCount());
            }
            var doc = new Document(map, protoDoc.getTitle(), protoDoc.getFilename());
            var passages = new ArrayList<Passage>();
            for (var protoPas : protoDoc.getPassagesList()) {
                var passageMap = new HashMap<String, Integer>();
                for (NordjyskeDatasetProtos.Word word : protoPas.getWordsList()) {
                    passageMap.put(word.getWord(), word.getAppearanceCount());
                }
                Passage passage = new Passage(passageMap);
                passage.setText(protoPas.getText());
                passages.add(passage);
            }
            doc.setPassages(passages);
            docs.add(doc);
        }
        return docs;
    }

    public HashMap<String, Integer> loadCollectionWordList(String filename) throws IOException {
        var map = new HashMap<String, Integer>();
        var wordList = NordjyskeDatasetProtos.Collection.parseFrom(new FileInputStream(filename)).getWordsList();
        for (NordjyskeDatasetProtos.Word word : wordList) {
            map.put(word.getWord(), word.getAppearanceCount());
        }
        return map;
    }

    private Wrapper parseJsonFile(File jsonFile) {
        Gson gson = new Gson();

        String fileText = null;
        try {
            fileText = new String(Files.readAllBytes(jsonFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gson.fromJson(fileText, Wrapper.class);
    }

    private static final class Wrapper {
        private String type;
        private Publication content;
        public Wrapper(){

        }
    }

    private static final class Publication {
        private String publisher;
        private String published_at;
        private String publication;
        private ArrayList<Article> articles;
        public Publication() {

        }
    }

    private static final class Article {
        private String extracted_from;
        private float confidence;
        private String headline;
        private String subhead;
        private ArrayList<Paragraph> paragraphs;

        public Article() {

        }
    }

    private static final class Paragraph {
        private String value;
        public Paragraph() {

        }
    }

    public static class Collection implements IPassageRetriever.ICollection {
        private final Map<String, Integer> wordList;
        private final long wordCount;
        private final long vocabularySize;
        private ArrayList<? extends IPassageRetriever.IDocument> documents;

        public Collection(Map<String, Integer> wordList) {
            this.wordList = wordList;
            this.vocabularySize = wordList.keySet().size();
            this.documents = null;
            int wordCount = 0;
            for (Map.Entry<String, Integer> entry : wordList.entrySet()) {
                wordCount += entry.getValue();
            }
            this.wordCount = wordCount;
        }

        @Override
        public long countWord(String word) {
            return wordList.getOrDefault(word, 0);
        }

        @Override
        public long getWordCount() {
            return this.wordCount;
        }

        @Override
        public long getVocabularySize() {
            return this.vocabularySize;
        }

        @Override
        public ArrayList<? extends IPassageRetriever.IDocument> getDocuments() {
            return this.documents;
        }

        @Override
        public boolean hasDocuments() {
            return this.documents != null;
        }

        public void setDocuments(ArrayList<? extends IPassageRetriever.IDocument> documents) {
            this.documents = documents;
        }
    }

    public static class Document implements IPassageRetriever.IDocument {
        private final Map<String, Integer> wordList;
        private final long wordCount;
        private final String title;
        private final String sourceFile;
        private ArrayList<? extends IPassageRetriever.IPassage> passages;

        public Document(Map<String, Integer> wordList, String title, String sourceFile) {
            this.wordList = wordList;
            this.title = title;
            this.sourceFile = sourceFile;
            int wordCount = 0;
            for (Map.Entry<String, Integer> entry : wordList.entrySet()) {
                wordCount += entry.getValue();
            }
            this.wordCount = wordCount;
        }

        @Override
        public long countWord(String word) {
            return wordList.getOrDefault(word, 0);
        }

        @Override
        public long getWordCount() {
            return this.wordCount;
        }

        @Override
        public ArrayList<? extends IPassageRetriever.IPassage> getPassages() {
            return this.passages;
        }

        @Override
        public boolean hasPassages() {
            return this.passages != null;
        }

        @Override
        public String getTitle() {
            return title;
        }

        public String getSourceFile() {
            return sourceFile;
        }

        public void setPassages(ArrayList<? extends IPassageRetriever.IPassage> passages) {
            this.passages = passages;
        }
    }

    public static class Passage implements IPassageRetriever.IPassage {
        private final Map<String, Integer> wordList;
        private final long wordCount;
        private String text;
        public Passage(Map<String, Integer> wordList) {
            this.wordList = wordList;
            this.text = "";
            int wordCount = 0;
            for (Map.Entry<String, Integer> entry : wordList.entrySet()) {
                wordCount += entry.getValue();
            }
            this.wordCount = wordCount;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public long countWord(String word) {
            return wordList.getOrDefault(word, 0);
        }

        @Override
        public long getWordCount() {
            return this.wordCount;
        }

        @Override
        public String toString() {
            return "Passage{" +
                    "wordList=" + wordList +
                    ", wordCount=" + wordCount +
                    '}';
        }
    }
}
