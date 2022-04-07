package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {

    @Test
    public void constructorTest() {
        Score score = new Score("username", 100);
        assertNotNull(score);
        assertEquals("username", score.username);
        assertEquals(100, score.score);
    }

    @Test
    public void testSameHashCode() {
        Score score1 = new Score("username", 100);
        Score score2 = new Score("username", 100);
        assertEquals(score1.hashCode(), score2.hashCode());
    }

    @Test
    public void testDifferentHashCode() {
        Score score1 = new Score("username", 100);
        Score score2 = new Score("username", 10);
        assertNotEquals(score1.hashCode(), score2.hashCode());
    }

    @Test
    public void testEqualsTrue() {
        Score score1 = new Score("username", 100);
        Score score2 = new Score("username", 100);
        assertEquals(score1, score2);
    }

    @Test
    public void testEqualsDifferentUser() {
        Score score1 = new Score("username1", 100);
        Score score2 = new Score("username2", 100);
        assertNotEquals(score1, score2);
    }

    @Test
    public void testEqualsDifferentScore() {
        Score score1 = new Score("username", 10);
        Score score2 = new Score("username", 100);
        assertNotEquals(score1, score2);
    }


    @Test
    public void toStringTest() {
        Score s = new Score("username", 100);
        String actual = s.toString();
        assertEquals(ToStringBuilder.reflectionToString(s, MULTI_LINE_STYLE), actual);
    }

}
