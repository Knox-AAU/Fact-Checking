import com.trippple.utils.StringUtils;
import org.junit.Test;
import org.junit.Before;

import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilsTest {
    private ResourceBundle paragraphs;

    @Before
    public void setUp() {
        paragraphs = ResourceBundle.getBundle("paragraphs");
    }

    @Test
    public void wordAppearanceCountReturnsCorrectCount() {
        String testString = "this is a test string, test";
        assertEquals(2, StringUtils.wordAppearanceCount("test", testString.split(" ")));
    }

    @Test
    public void wordAppearanceCount_ReturnsCorrectCount_MultipleWordsInNeedle() {
        var testStringArray = "test string with multiple words test string".split(" ");
        var expected = 4;

        var actual = StringUtils.wordAppearanceCount("testString", testStringArray);

        assertEquals(expected, actual);
    }

    @Test
    public void stringSplittingReturnsCorrectArray() {
        String testString = paragraphs.getString("DonaldIsPresident");

        String[] wordArray = StringUtils.splitStringToArray(testString);

        assertEquals(32, wordArray.length);
        assertEquals("past", wordArray[wordArray.length - 1]);
        assertEquals("Donald", wordArray[0]);
    }

    @Test
    public void stringSplitterIncludesHyphen() {
        String testString = paragraphs.getString("ObamaAfricanAmerican");

        String[] wordArray = StringUtils.splitStringToArray(testString);

        assertEquals("African-American", wordArray[12]);
    }

    @Test
    public void splitByCapital_StringWithCapitals_ReturnsCorrectStringArray() {
        String testString = "stringSplitByCapital skulle gerne splittes";

        String[] actual = StringUtils.splitByCapital(testString);
        String[] expected = {"string", "Split", "By", "Capital skulle gerne splittes"};

        assertArrayEquals(expected, actual);
    }

    @Test
    public void splitByCapitalAndMakeLower_StringWithCapitals_ReturnsCorrectStringArray() {
        String testString = "stringSplitByCapital skulle gerne splittes og laves lower";

        String[] actual = StringUtils.splitByCapitalAndMakeLower(testString);
        String[] expected = {"string", "split", "by", "capital skulle gerne splittes og laves lower"};

        assertArrayEquals(expected, actual);
    }

    @Test
    public void splitByCapital_stringWithNoCapitals_ReturnsCorrectStringArray() {
        String testString = "string split by capital skulle gerne splittes";

        String[] actual = StringUtils.splitByCapital(testString);
        String[] expected = {"string split by capital skulle gerne splittes"};

        assertArrayEquals(expected, actual);
    }

    @Test
    public void splitByCapital_EmptyString_ReturnsEmptyString() {
        String testString = "";

        String[] actual = StringUtils.splitByCapital(testString);
        String[] expected = {""};

        assertArrayEquals(expected, actual);
    }
}