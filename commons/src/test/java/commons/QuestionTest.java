package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void testConstructors() {
        Activity activity1 = new Activity("1","/path/to/image/","Activity", 200);
        Activity activity2 = new Activity("2","/path/to/image/","Activity", 200);
        Activity activity3 = new Activity("3","/path/to/image/","Activity", 200);
        Activity activity4 = new Activity("4","/path/to/image/","Activity", 200);

        Question generalQuestion = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion = new EstimationQuestion(activity1);

        assertNotNull(generalQuestion);
        assertNotNull(comparisonQuestion);
        assertNotNull(estimationQuestion);
    }

    @Test
    void testDisplayQuestion() {
        Activity activity1 = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        Activity activity2 = new Activity("2","/path/to/image/","Activity", 200);
        Activity activity3 = new Activity("3","/path/to/image/","Activity", 200);
        Activity activity4 = new Activity("4","/path/to/image/","Activity", 200);

        Question generalQuestion = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion = new EstimationQuestion(activity1);

        assertEquals("How much energy does taking a shower for 2 minutes take?", generalQuestion.displayQuestion());
        assertEquals("Instead of taking a shower for 2 minutes, you could use the same amount of energy for...", comparisonQuestion.displayQuestion());
        assertEquals("How much energy does taking a shower for 2 minutes take?", estimationQuestion.displayQuestion());
    }

    @Test
    void testEquals() {
        Activity activity1 = new Activity("1","/path/to/image/","Activity", 200);
        Activity activity2 = new Activity("2","/path/to/image/","Activity", 200);
        Activity activity3 = new Activity("3","/path/to/image/","Activity", 200);
        Activity activity4 = new Activity("4","/path/to/image/","Activity", 200);

        Question generalQuestion = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion = new EstimationQuestion(activity1);

        assertEquals(generalQuestion, generalQuestion);
        assertEquals(comparisonQuestion, comparisonQuestion);
        assertEquals(estimationQuestion, estimationQuestion);

        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        generalQuestion2.questionId = generalQuestion.questionId;
        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;
        Question estimationQuestion2 = new EstimationQuestion(activity1);
        estimationQuestion2.questionId = estimationQuestion.questionId;

        assertEquals(generalQuestion, generalQuestion2);
        assertEquals(comparisonQuestion, comparisonQuestion2);
        assertEquals(estimationQuestion, estimationQuestion2);
    }

    @Test
    void testEquals2() {
        Activity activity1 = new Activity("1","/path/to/image/","Activity", 200);
        Activity activity2 = new Activity("2","/path/to/image/","Activity", 200);
        Activity activity3 = new Activity("3","/path/to/image/","Activity", 200);
        Activity activity4 = new Activity("4","/path/to/image/","Activity", 200);

        Activity activity12 = new Activity("1","/path/to/image/","Activity", 200);
        Activity activity22 = new Activity("2","/path/to/image/","Activity", 200);
        Activity activity32 = new Activity("3","/path/to/image/","Activity", 200);
        Activity activity42 = new Activity("4","/path/to/image/","Activity", 200);

        Question generalQuestion = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion = new EstimationQuestion(activity1);

        Question generalQuestion2 = new GeneralQuestion(activity12, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        generalQuestion2.questionId = generalQuestion.questionId;
        Question comparisonQuestion2 = new ComparisonQuestion(activity12, List.of(activity22, activity32, activity42), 3);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;
        Question estimationQuestion2 = new EstimationQuestion(activity12);
        estimationQuestion2.questionId = estimationQuestion.questionId;

        assertEquals(generalQuestion, generalQuestion2);
        assertEquals(comparisonQuestion, comparisonQuestion2);
        assertEquals(estimationQuestion, estimationQuestion2);
    }

    @Test
    void testNotEquals() {
        Activity activity1 = new Activity("1","/path/to/image/","Activity", 200);
        Activity activity2 = new Activity("2","/path/to/image/","Activity", 200);
        Activity activity3 = new Activity("3","/path/to/image/","Activity", 200);
        Activity activity4 = new Activity("4","/path/to/image/","Activity", 200);

        Question generalQuestion = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion = new EstimationQuestion(activity1);

        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion2 = new EstimationQuestion(activity1);

        assertNotEquals(generalQuestion, generalQuestion2);
        assertNotEquals(comparisonQuestion, comparisonQuestion2);
        assertNotEquals(estimationQuestion, estimationQuestion2);
    }

    @Test
    void testHashCode() {
        Activity activity1 = new Activity("1","/path/to/image/","Activity", 200);
        Activity activity2 = new Activity("2","/path/to/image/","Activity", 200);
        Activity activity3 = new Activity("3","/path/to/image/","Activity", 200);
        Activity activity4 = new Activity("4","/path/to/image/","Activity", 200);

        Question generalQuestion = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion = new EstimationQuestion(activity1);

        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        generalQuestion2.questionId = generalQuestion.questionId;
        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;
        Question estimationQuestion2 = new EstimationQuestion(activity1);
        estimationQuestion2.questionId = estimationQuestion.questionId;

        assertEquals(generalQuestion.hashCode(), generalQuestion2.hashCode());
        assertEquals(comparisonQuestion.hashCode(), comparisonQuestion2.hashCode());
        assertEquals(estimationQuestion.hashCode(), estimationQuestion2.hashCode());
    }

    @Test
    void testToString() {
        Activity activity1 = new Activity("1","/path/to/image/","Activity", 200);
        Activity activity2 = new Activity("2","/path/to/image/","Activity", 200);
        Activity activity3 = new Activity("3","/path/to/image/","Activity", 200);
        Activity activity4 = new Activity("4","/path/to/image/","Activity", 200);

        Question generalQuestion = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion = new EstimationQuestion(activity1);

        assertEquals(ToStringBuilder.reflectionToString(generalQuestion, MULTI_LINE_STYLE), generalQuestion.toString());
        assertEquals(ToStringBuilder.reflectionToString(comparisonQuestion, MULTI_LINE_STYLE), comparisonQuestion.toString());
        assertEquals(ToStringBuilder.reflectionToString(estimationQuestion, MULTI_LINE_STYLE), estimationQuestion.toString());
    }
}