import com.trippple.processing.Stemmer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StemmerTest {
    @Autowired
    private Stemmer stemmer;

    @Test
    public void stem_ReturnsStemmedWord_StringWithOneWord() throws Exception {
        String testWord = "kanonerne";
        String expected = "kanon";

        String actual = stemmer.stem(testWord);

        assertEquals(expected, actual);
    }

    @Test
    public void stem_ReturnsStemmedWordToLower_StringInAllCapital() throws Exception {
        String testWord = "KANONERNE";
        String expected = "kanon";

        String actual = stemmer.stem(testWord);

        assertEquals(expected, actual);
    }

    @Test
    public void stem_ReturnsException_UnknownWord() {
        String testWord = "Dioter";
        String expected = "Dioter";

        String actual = stemmer.stem(testWord);

        assertEquals(expected, actual);
    }

    @Test
    public void stem_ReturnsException_EmptyString() {
        String testWord = "";
        String expected = "";

        String actual = stemmer.stem(testWord);

        assertEquals(expected, actual);
    }
}