package com.trippple.datasources;

import com.trippple.models.Flexion;

import java.io.IOException;
import java.util.List;

public interface IFlexionDataSource {
    List<Flexion> getAll() throws IOException;
}
