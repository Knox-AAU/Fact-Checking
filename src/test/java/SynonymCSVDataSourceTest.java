import com.trippple.datasources.ISynonymSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SynonymCSVDataSourceTest {
    @Autowired
    private ISynonymSource synonymSource;

    @Test
    public void getSynonyms_ReturnsCorrectSynonyms_WordInFile() {
        var testWord = "glad";
        var expectedSynonyms = new String[]{"fornøjet", "lystig", "munter", "ubekymret"};

        var actualSynonyms = synonymSource.getSynonyms(testWord).toArray();

        assertArrayEquals(expectedSynonyms, actualSynonyms);
    }

    @Test
    public void getSynonyms_ReturnsEmptyArray_WordNotInFile() {
        var testWord = "definitelyNotInFile";
        var expectedSynonyms = new String[0];

        var actualSynonyms = synonymSource.getSynonyms(testWord).toArray();

        assertArrayEquals(expectedSynonyms, actualSynonyms);
    }

    @Test
    public void getSynonyms_ReturnsEmptyArray_StringIsNull() {
        String testWord = null;
        var expectedSynonyms = new String[0];

        var actualSynonyms = synonymSource.getSynonyms(testWord).toArray();

        assertArrayEquals(expectedSynonyms, actualSynonyms);
    }

    @Test
    public void getSynonyms_ReturnsEmptyArray_StringIsEmpty() {
        String testWord = "";
        var expectedSynonyms = new String[0];

        var actualSynonyms = synonymSource.getSynonyms(testWord).toArray();

        assertArrayEquals(expectedSynonyms, actualSynonyms);
    }
}
