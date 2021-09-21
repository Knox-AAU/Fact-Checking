package com.trippple.repositories;

import com.trippple.models.Document;
import com.trippple.datasources.IDocumentDataSource;

import java.util.List;

public class DocumentRepository {
    private final IDocumentDataSource dataSource;

    public DocumentRepository(IDocumentDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Document> getAllDocuments() {
        return dataSource.getAll();
    }
}
