package com.trippple;

import com.trippple.algorithms.TMWIIS;
import com.trippple.datasources.*;
import com.trippple.processing.Stemmer;
import com.trippple.processing.Synonym;
import com.trippple.repositories.FlexionRepository;
import com.trippple.repositories.StopWordRepository;
import com.trippple.processing.NordjyskeDatasetProcessing;
import com.trippple.repositories.SynonymRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

@Configuration
@ComponentScan(basePackageClasses = Application.class)
public class Config {
    private final Properties config;
    public Config() throws IOException {
        config = Config.loadConfiguration();
    }

    @Qualifier("synonyms")
    @Bean
    public ISynonymSource synonymSource() {
        return new SynonymCSVDataSource(Path.of(config.getProperty("ddoSynonymsFilePath")));
    }

    @Qualifier("documents")
    @Bean
    public IDocumentDataSource documentDataSource() throws IOException {
        return new DocumentKnoxServerDataSource(config.getProperty("datasetDirectoryPath"));
    }

    @Qualifier("stopwords")
    @Bean
    public IStopWordDataSource stopWordDataSource() {
        return new DanishStopWordsDatasource(config.getProperty("stopWordsFilePath"));
    }

    @Bean
    public Benchmark benchmark() {
        return new Benchmark();
    }

    @Qualifier("flexikonDatasource")
    @Bean
    public IFlexionDataSource flexionDataSource() throws IOException {
        return new DslFlexiconDataSource(new File(config.getProperty("flexikonFilePath")));
    }

    @Bean
    public FlexionRepository flexionRepository(
            @Qualifier("flexikonDatasource") IFlexionDataSource flexionDataSource
    ) {
        return new FlexionRepository(flexionDataSource);
    }

    @Bean
    public Stemmer stemmer(
            FlexionRepository flexionRepository
    ) {
        return new Stemmer(flexionRepository);
    }

    @Bean
    public Application trippple(
            @Qualifier("TMWIISPassageRetriever") IPassageRetriever passageRetriever,
            NordjyskeDatasetProcessing processor,
            Stemmer stemmer,
            Benchmark benchmark,
            Synonym synonym,
            StopWordRepository stopWordRepository
    ) {
        return new Application(passageRetriever, stemmer, benchmark, synonym, stopWordRepository);
    }

    @Bean
    public NordjyskeDatasetProcessing nordjyskeDatasetProcessing(
            Stemmer stemmer
    ) {
        return new NordjyskeDatasetProcessing(stemmer);
    }

    @Bean
    public TMWIIS tmwiis() {
        return new com.trippple.algorithms.TMWIIS();
    }

    @Bean
    public StopWordRepository stopWordRepository(
            IStopWordDataSource stopWordDataSource
    ) {
        return new StopWordRepository(stopWordDataSource);
    }

    @Qualifier("TMWIISPassageRetriever")
    @Bean
    public IPassageRetriever passageRetriever(
            TMWIIS algo,
            NordjyskeDatasetProcessing processing
    ) throws IOException {
        return new TMWIISPassageRetriever(algo, processing);
    }

    public static Properties loadConfiguration() throws IOException {
        var configInputStream = com.trippple.Config.class.getClassLoader().getResourceAsStream("configuration.xml");
        var config = new Properties();
        config.loadFromXML(configInputStream);
        return config;
    }

    @Bean
    public SynonymRepository synonymRepository(
            ISynonymSource synonymSource
    ) {
        return new SynonymRepository(synonymSource);
    }

    @Bean
    public Synonym synonym(
            SynonymRepository synonymRepository
    ) {
        return new Synonym(synonymRepository);
    }
}
