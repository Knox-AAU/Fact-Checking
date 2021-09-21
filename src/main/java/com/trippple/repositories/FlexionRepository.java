package com.trippple.repositories;

import com.trippple.datasources.IFlexionDataSource;
import com.trippple.models.FlexWord;
import com.trippple.models.Flexion;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class FlexionRepository {
    private final IFlexionDataSource flexionDataSource;
    private List<Flexion> cachedFlexions = null;
    private Map<String, Flexion> flexionMap = null;

    public FlexionRepository(IFlexionDataSource dataSource) {
        flexionDataSource = dataSource;
    }

    public List<Flexion> getAll() {
        if (cachedFlexions == null) {
            try {
                cachedFlexions = flexionDataSource.getAll();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return cachedFlexions;
    }

    public Flexion getFlexionForWord(String givenWord) {
        if (flexionMap == null) {
            flexionMap = initHashMap(getAll());
        }
        return flexionMap.get(givenWord.toLowerCase());
    }

    private Map<String, Flexion> initHashMap(List<Flexion> flexions) {
        var map = new HashMap<String, Flexion>();
        for (Flexion flexion : flexions) {
            var words = flexion.getWords();
            for (FlexWord flexWord : words) {
                map.put(flexWord.getWord().toLowerCase(), flexion);
            }
        }
        return map;
    }
}
