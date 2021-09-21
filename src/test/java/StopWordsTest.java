import com.trippple.repositories.StopWordRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StopWordsTest {
    @Autowired
    private StopWordRepository stopWordRepository;

    @Test
    public void isStopWord_ReturnsTrueForFoundStopWord_LowerCaseWord() throws IOException {
        String testWord = "er";
        boolean expected = true;

        boolean actual = stopWordRepository.isStopWord(testWord);

        assertEquals(expected, actual);
    }

    @Test
    public void isStopWord_ReturnsTrueForFoundStopWord_UpperCaseWord() throws IOException {
        String testWord = "ER";
        boolean expected = true;

        boolean actual = stopWordRepository.isStopWord(testWord);

        assertEquals(expected, actual);
    }

    @Test
    public void isStopWord_ReturnsFalseIfNotStopWord_LowerCaseWord() throws IOException {
        String testWord = "præsident";
        boolean expected = false;

        boolean actual = stopWordRepository.isStopWord(testWord);

        assertEquals(expected, actual);
    }

    @Test
    public void isStopWord_ReturnsFalseIfNotStopWord_EmptyString() throws IOException {
        String testWord = "";
        boolean expected = false;

        boolean actual = stopWordRepository.isStopWord(testWord);

        assertEquals(expected, actual);
    }

    @Test
    public void isStopWord_ReturnsFalseIfNotStopWord_Null() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> {
            stopWordRepository.isStopWord(null);
        });
    }

    @Test
    public void RemoveStopWords_ReturnsListWithoutStopWords_LowerCaseWords() throws IOException {
        var testList = Arrays.asList("Donald", "Trump", "er", "Præsident");
        var expected = Arrays.asList("Donald", "Trump", "Præsident");

        var actual = stopWordRepository.removeStopWords(testList);

        assertEquals(expected, actual);
    }

    @Test
    public void RemoveStopWords_ReturnsListWithoutStopWords_UpperCaseWords() throws IOException {
        var testList = Arrays.asList("DONALD", "TRUMP", "ER", "PRÆSIDENT");
        var expected = Arrays.asList("DONALD", "TRUMP", "PRÆSIDENT");

        var actual = stopWordRepository.removeStopWords(testList);

        assertEquals(expected, actual);
    }

    @Test
    public void RemoveStopWords_ReturnsListWithoutStopWords_MoreStopWords() throws IOException {
        var testList = Arrays.asList("Donald", "Trump", "er", "ikke", "Præsident", "aldrig");
        var expected = Arrays.asList("Donald", "Trump", "Præsident");

        var actual = stopWordRepository.removeStopWords(testList);

        assertEquals(expected, actual);
    }

    @Test
    public void RemoveStopWords_ReturnsEmptyList_EmptyList() throws IOException {
        var testList = new ArrayList<String>();
        var expected = new ArrayList<String>();

        var actual = stopWordRepository.removeStopWords(testList);

        assertEquals(expected, actual);
    }

    @Test
    public void RemoveStopWords_ReturnsEmptyList_Null() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> {
            stopWordRepository.removeStopWords(null);
        });
    }
}
