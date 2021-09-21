package com.trippple.datasources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class SynonymCSVDataSource implements ISynonymSource {
    private final Path csvFilePath;
    private final Map<String, List<String>> synonymMap = new HashMap<>();

    public SynonymCSVDataSource(Path csvFilePath) {
        this.csvFilePath = csvFilePath;
        mapFile();
    }

    private void mapFile() {
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toString(), StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                var keyVal = Arrays.asList(line.split("\t"));
                synonymMap.put(keyVal.get(0), Arrays.asList(keyVal.get(1).split(";")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getSynonyms(String word) {
        var val = synonymMap.get(word);
        return (val == null) ? Collections.emptyList() : val;
    }
}
