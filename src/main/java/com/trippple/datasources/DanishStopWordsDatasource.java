package com.trippple.datasources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class DanishStopWordsDatasource implements IStopWordDataSource {
    private final String filePath;

    public DanishStopWordsDatasource(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Set<String> getAll() throws IOException {
        var stopWords = new HashSet<String>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.map(String::toLowerCase).forEach(stopWords::add);
        }
        return stopWords;
    }
}
