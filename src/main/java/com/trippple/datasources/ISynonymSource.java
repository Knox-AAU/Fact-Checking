package com.trippple.datasources;

import java.util.List;

public interface ISynonymSource {
    List<String> getSynonyms(String word);
}
