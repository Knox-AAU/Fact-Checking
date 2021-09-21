package com.trippple.datasources;

import com.trippple.models.Document;

import java.util.List;

public interface IDocumentDataSource {
    List<Document> getAll();
}
