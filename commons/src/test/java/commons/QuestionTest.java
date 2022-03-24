package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    private Activity activity1;
    private Activity activity2;
    private Activity activity3;
    private Activity activity4;

    private Question generalQuestion;
    private Question comparisonQuestion;
    private Question estimationQuestion;
    private Question whichIsMoreQuestion;

    @BeforeEach
    public void setup() {

        this.activity1 = new Activity("1","/path/to/image/","Activity1", 200);
        this.activity2 = new Activity("2","/path/to/image/","Activity2", 200);
        this.activity3 = new Activity("3","/path/to/image/","Activity3", 200);
        this.activity4 = new Activity("4","/path/to/image/","Activity4", 200);

        this.generalQuestion = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        this.comparisonQuestion = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        this.estimationQuestion = new EstimationQuestion(activity1);
        this.whichIsMoreQuestion = new WhichIsMoreQuestion(List.of(activity1,activity2,activity3),1);

    }

    @Test
    void testConstructors() {
        assertNotNull(generalQuestion);
        assertNotNull(comparisonQuestion);
        assertNotNull(estimationQuestion);
        assertNotNull(whichIsMoreQuestion);
    }

    @Test
    void testDisplayQuestion() {
        Activity activityNamed = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);

        Question generalQuestion = new GeneralQuestion(activityNamed, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion = new ComparisonQuestion(activityNamed, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion = new EstimationQuestion(activityNamed);

        assertEquals("How much energy does taking a shower for 2 minutes take?", generalQuestion.displayQuestion());
        assertEquals("Instead of taking a shower for 2 minutes, you could use the same amount of energy for...", comparisonQuestion.displayQuestion());
        assertEquals("How much energy does taking a shower for 2 minutes take?", estimationQuestion.displayQuestion());
        assertEquals("Which activity consumes more energy?", whichIsMoreQuestion.displayQuestion());
    }

    @Test
    void testEquals() {
        // Tests equality with the same attributes and instantiation.

        assertEquals(generalQuestion, generalQuestion);
        assertEquals(comparisonQuestion, comparisonQuestion);
        assertEquals(estimationQuestion, estimationQuestion);
        assertEquals(whichIsMoreQuestion,whichIsMoreQuestion);

        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        generalQuestion2.questionId = generalQuestion.questionId;
        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;
        Question estimationQuestion2 = new EstimationQuestion(activity1);
        estimationQuestion2.questionId = estimationQuestion.questionId;
        Question whichIsMoreQuestion2 = new WhichIsMoreQuestion(List.of(activity1,activity2,activity3),1);
        whichIsMoreQuestion2.questionId = whichIsMoreQuestion.questionId;

        assertEquals(generalQuestion, generalQuestion2);
        assertEquals(comparisonQuestion, comparisonQuestion2);
        assertEquals(estimationQuestion, estimationQuestion2);
        assertEquals(whichIsMoreQuestion,whichIsMoreQuestion2);
    }

    @Test
    void testEquals2() {
        // Tests equality with new Activity objects, apart from that the Question objects are the same.

        Activity activity12 = new Activity("1","/path/to/image/","Activity1", 200);
        Activity activity22 = new Activity("2","/path/to/image/","Activity2", 200);
        Activity activity32 = new Activity("3","/path/to/image/","Activity3", 200);
        Activity activity42 = new Activity("4","/path/to/image/","Activity4", 200);

        Question generalQuestion2 = new GeneralQuestion(activity12, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        generalQuestion2.questionId = generalQuestion.questionId;
        Question comparisonQuestion2 = new ComparisonQuestion(activity12, List.of(activity22, activity32, activity42), 3);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;
        Question estimationQuestion2 = new EstimationQuestion(activity12);
        estimationQuestion2.questionId = estimationQuestion.questionId;
        Question whichIsMoreQuestion2 = new WhichIsMoreQuestion(List.of(activity12,activity22,activity32),1);
        whichIsMoreQuestion2.questionId = whichIsMoreQuestion.questionId;

        assertEquals(generalQuestion, generalQuestion2);
        assertEquals(comparisonQuestion, comparisonQuestion2);
        assertEquals(estimationQuestion, estimationQuestion2);
        assertEquals(whichIsMoreQuestion,whichIsMoreQuestion2);
    }

    @Test
    void testEqualsDifferentAnswerOrder() {
        // Tests equality for when the answer options are in a different order, but the actual answer is the same (and
        // the contents of the answer lists are the same).

        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("10 Wh", "200 Wh", "5 Wh"), 2);
        generalQuestion2.questionId = generalQuestion.questionId;

        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity3, activity4, activity2), 2);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;

        Question whichIsMoreQuestion2 = new WhichIsMoreQuestion(List.of(activity2, activity1, activity3),2);
        whichIsMoreQuestion2.questionId = whichIsMoreQuestion.questionId;

        assertEquals(generalQuestion, generalQuestion2);
        assertEquals(comparisonQuestion, comparisonQuestion2);
        assertEquals(whichIsMoreQuestion,whichIsMoreQuestion2);
    }

    @Test
    void testNotEqualsDifferentAnswerOrderAndAnswer() {
        // Tests IN-equality for when the answer options are in a different order, and the actual answer is different.

        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("10 Wh", "200 Wh", "5 Wh"), 3);
        generalQuestion2.questionId = generalQuestion.questionId;

        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity2, activity4, activity3), 3);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;

        Question whichIsMoreQuestion2 = new WhichIsMoreQuestion(List.of(activity2, activity1, activity4),3);
        whichIsMoreQuestion2.questionId = whichIsMoreQuestion.questionId;

        assertNotEquals(generalQuestion, generalQuestion2);
        assertNotEquals(comparisonQuestion, comparisonQuestion2);
        assertNotEquals(whichIsMoreQuestion,whichIsMoreQuestion2);
    }

    @Test
    void testNotEqualsAnswersOutOfRange() {
        // Tests IN-equality for when the answer index is out of range.

        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 4);
        generalQuestion2.questionId = generalQuestion.questionId;
        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 4);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;
        Question whichIsMoreQuestion2 = new WhichIsMoreQuestion(List.of(activity1,activity2,activity3),4);
        whichIsMoreQuestion2.questionId = whichIsMoreQuestion.questionId;

        assertNotEquals(generalQuestion, generalQuestion2);
        assertNotEquals(comparisonQuestion, comparisonQuestion2);
        assertNotEquals(whichIsMoreQuestion,whichIsMoreQuestion2);
    }

    @Test
    void testEqualsAnswersOutOfRange() {
        // Tests equality for when the answer index is out of range.

        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 4);
        generalQuestion2.questionId = generalQuestion.questionId;
        Question generalQuestion3 = new GeneralQuestion(activity1, List.of("5 Wh", "200 Wh", "10 Wh"), 6);
        generalQuestion3.questionId = generalQuestion.questionId;
        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 4);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;
        Question comparisonQuestion3 = new ComparisonQuestion(activity1, List.of(activity2, activity4, activity3), 6);
        comparisonQuestion3.questionId = comparisonQuestion.questionId;
        Question whichIsMoreQuestion2 = new WhichIsMoreQuestion(List.of(activity1,activity3,activity2),4);
        whichIsMoreQuestion2.questionId = whichIsMoreQuestion.questionId;
        Question whichIsMoreQuestion3 = new WhichIsMoreQuestion(List.of(activity2,activity3,activity1),6);
        whichIsMoreQuestion3.questionId = whichIsMoreQuestion.questionId;

        assertEquals(generalQuestion3, generalQuestion2);
        assertEquals(comparisonQuestion3, comparisonQuestion2);
        assertEquals(whichIsMoreQuestion3,whichIsMoreQuestion2);
    }

    @Test
    void testNotEquals() {
        // Test general IN-equality (the ids are not equal).

        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        Question estimationQuestion2 = new EstimationQuestion(activity1);
        Question whichIsMoreQuestion2 = new WhichIsMoreQuestion(List.of(activity1,activity2,activity3),1);

        assertNotEquals(generalQuestion, generalQuestion2);
        assertNotEquals(comparisonQuestion, comparisonQuestion2);
        assertNotEquals(estimationQuestion, estimationQuestion2);
        assertNotEquals(whichIsMoreQuestion,whichIsMoreQuestion2);
    }

    @Test
    void testHashCode() {
        Question generalQuestion2 = new GeneralQuestion(activity1, List.of("5 Wh", "10 Wh", "200 Wh"), 3);
        generalQuestion2.questionId = generalQuestion.questionId;
        Question comparisonQuestion2 = new ComparisonQuestion(activity1, List.of(activity2, activity3, activity4), 3);
        comparisonQuestion2.questionId = comparisonQuestion.questionId;
        Question estimationQuestion2 = new EstimationQuestion(activity1);
        estimationQuestion2.questionId = estimationQuestion.questionId;
        Question whichIsMoreQuestion2 = new WhichIsMoreQuestion(List.of(activity1,activity2,activity3),1);
        whichIsMoreQuestion2.questionId = whichIsMoreQuestion.questionId;

        assertEquals(generalQuestion.hashCode(), generalQuestion2.hashCode());
        assertEquals(comparisonQuestion.hashCode(), comparisonQuestion2.hashCode());
        assertEquals(estimationQuestion.hashCode(), estimationQuestion2.hashCode());
        assertEquals(whichIsMoreQuestion.hashCode(),whichIsMoreQuestion2.hashCode());
    }

    @Test
    void testToString() {
        assertEquals(ToStringBuilder.reflectionToString(generalQuestion, MULTI_LINE_STYLE), generalQuestion.toString());
        assertEquals(ToStringBuilder.reflectionToString(comparisonQuestion, MULTI_LINE_STYLE), comparisonQuestion.toString());
        assertEquals(ToStringBuilder.reflectionToString(estimationQuestion, MULTI_LINE_STYLE), estimationQuestion.toString());
        assertEquals(ToStringBuilder.reflectionToString(estimationQuestion, MULTI_LINE_STYLE), estimationQuestion.toString());
    }
}