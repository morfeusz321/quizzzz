package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnswerResponseEntityTest {

    @Test
    public void checkConstructor1() {
        AnswerResponseEntity answerResponseEntity = new AnswerResponseEntity(true);
        assertTrue(answerResponseEntity.correct);
    }

    @Test
    public void checkConstructor2() {
        AnswerResponseEntity answerResponseEntity = new AnswerResponseEntity(false, 5);
        assertFalse(answerResponseEntity.correct);
        assertEquals(5, answerResponseEntity.proximity);
    }

    @Test
    public void equalsHashCode() {
        AnswerResponseEntity answerResponseEntity1 = new AnswerResponseEntity(false, 5);
        AnswerResponseEntity answerResponseEntity2 = new AnswerResponseEntity(false, 5);
        assertEquals(answerResponseEntity1, answerResponseEntity2);
        assertEquals(answerResponseEntity1.hashCode(), answerResponseEntity2.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        AnswerResponseEntity answerResponseEntity1 = new AnswerResponseEntity(false, 5);
        AnswerResponseEntity answerResponseEntity2 = new AnswerResponseEntity(false, 6);
        assertNotEquals(answerResponseEntity1, answerResponseEntity2);
        assertNotEquals(answerResponseEntity1.hashCode(), answerResponseEntity2.hashCode());
    }

    @Test
    public void hasToString() {
        AnswerResponseEntity answerResponseEntity = new AnswerResponseEntity(false, 5);
        String s = answerResponseEntity.toString();
        assertTrue(s.contains(AnswerResponseEntity.class.getSimpleName()));
        assertTrue(s.contains("\n"));
        assertTrue(s.contains("correct"));
        assertTrue(s.contains("proximity"));
    }

}
