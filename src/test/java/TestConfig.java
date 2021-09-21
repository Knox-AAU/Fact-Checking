import com.trippple.IPassageRetriever;
import com.trippple.processing.NordjyskeDatasetProcessing;
import com.trippple.processing.Stemmer;
import com.trippple.TMWIISPassageRetriever;
import com.trippple.algorithms.TMWIIS;
import com.trippple.datasources.*;
import com.trippple.repositories.FlexionRepository;
import com.trippple.repositories.StopWordRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Configuration
public class TestConfig {
    @Qualifier("synonyms")
    @Bean
    public ISynonymSource synonymSource() {
        return new SynonymCSVDataSource(Path.of("src/test/resources/synonyms.csv"));
    }

    @Qualifier("flexions")
    @Bean
    public IFlexionDataSource flexionDataSource() throws IOException {
        return new DslFlexiconDataSource(new File("src/test/resources/flexicon.txt"));
    }

    @Qualifier("stopwords")
    @Bean
    public IStopWordDataSource stopWordDataSource(){
        return new DanishStopWordsDatasource("src/test/resources/stop-words_test.txt");
    }

    @Bean
    public Stemmer stemmer(IFlexionDataSource flexionDataSource) {
        return new Stemmer(new FlexionRepository(flexionDataSource));
    }

    @Bean
    public StopWordRepository stopWordRepository(
            IStopWordDataSource stopWordDataSource
    ) {
        return new StopWordRepository(stopWordDataSource);
    }

    @Bean
    public com.trippple.algorithms.TMWIIS tmwiis() {
        return new com.trippple.algorithms.TMWIIS();
    }

    @Bean
    public NordjyskeDatasetProcessing nordjyskeDatasetProcessing(
            Stemmer stemmer
    ) {
        return new NordjyskeDatasetProcessing(stemmer);
    }

    @Bean
    public IPassageRetriever passageRetriever(
            TMWIIS algo,
            NordjyskeDatasetProcessing processing
    ) throws IOException {
        return new TMWIISPassageRetriever(algo, processing);
    }
}
