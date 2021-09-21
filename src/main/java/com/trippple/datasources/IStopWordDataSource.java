package com.trippple.datasources;

import java.io.IOException;
import java.util.Set;

public interface IStopWordDataSource {
    Set<String> getAll() throws IOException;
}
