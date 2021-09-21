package com.trippple.datasources;

import com.google.gson.Gson;
import com.trippple.models.Document;
import com.trippple.models.Passage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DocumentKnoxServerDataSource implements IDocumentDataSource {
    private final String documentFolderPath;
    ArrayList<Document> documentCollection = null;
    private final Logger log;
    public DocumentKnoxServerDataSource(String documentFolderPath) {
        this.documentFolderPath = documentFolderPath;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public List<Document> getAll() {
        if (documentCollection != null) return documentCollection;

        File folder = new File(documentFolderPath);
        File[] listOfFiles = folder.listFiles();
        ArrayList<Document> documents = new ArrayList<>();

        int i = 0;

        for (File file : Objects.requireNonNull(listOfFiles)) {
            if (!file.isFile()) continue;

            log.debug("Processing file "+ i++);

            Wrapper wrapper = parseJsonFile(file);
            for (Article article : wrapper.content.articles) {
                ArrayList<Passage> paragraphs = new ArrayList<>();
                for (Paragraph paragraph : article.paragraphs) {
                    if (paragraph.value == null || paragraph.value.length() == 0) continue;
                    paragraphs.add(new Passage(paragraph.value));
                }
                documents.add(new Document(paragraphs));
            }
        }

        documentCollection = documents;

        return documents;
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

    private final class Wrapper {
        private String type;
        private Publication content;
        public Wrapper(){

        }
    }

    private final class Publication {
        private String publisher;
        private String published_at;
        private String publication;
        private ArrayList<Article> articles;
        public Publication() {

        }
    }

    private final class Article {
        private String extracted_from;
        private float confidence;
        private String headline;
        private String subhead;
        private ArrayList<Paragraph> paragraphs;

        public Article() {

        }
    }

    private final class Paragraph {
        private String value;
        public Paragraph() {

        }
    }
}
