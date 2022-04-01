package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnswerResponseEntityTest {

    @Test
    public void checkConstructor1() {
        AnswerResponseEntity answerResponseEntity = new AnswerResponseEntity(true, 1);
        assertTrue(answerResponseEntity.correct);
    }

    @Test
    public void checkGetter() {
        AnswerResponseEntity answerResponseEntity = new AnswerResponseEntity(true, 1);
        assertEquals(1, answerResponseEntity.getAnswer());
    }

    @Test
    public void checkConstructor2() {
        AnswerResponseEntity answerResponseEntity = new AnswerResponseEntity(true, 4, 2, 100);
        assertEquals(100, answerResponseEntity.getPoints());
    }

    @Test
    public void getPoints() {
        AnswerResponseEntity answerResponseEntity = new AnswerResponseEntity(false, 5);
        assertEquals(0, answerResponseEntity.getPoints());
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

    @Test
    public void dynamicPointsEstimationCorrect() {
        int points = AnswerResponseEntity.dynamicPointsEstimation(0, 100, 14520);
        assertEquals(100, points);
    }

    @Test
    public void dynamicPointsEstimationFalse() {
        int points = AnswerResponseEntity.dynamicPointsEstimation(70, 100, 14520);
        assertNotEquals(100, points);
    }

    @Test
    public void dynamicPointsMCCorrectFull() {
        int points = AnswerResponseEntity.dynamicPointsMultipleChoice(true, 14520);
        assertEquals(100, points);
    }

    @Test
    public void dynamicPointsMCCorrectPartial() {
        int points = AnswerResponseEntity.dynamicPointsMultipleChoice(true, 100);
        assertNotEquals(100, points);
    }

    @Test
    public void dynamicPointsMCFalse() {
        int points = AnswerResponseEntity.dynamicPointsMultipleChoice(false, 100);
        assertEquals(0, points);
    }

}
