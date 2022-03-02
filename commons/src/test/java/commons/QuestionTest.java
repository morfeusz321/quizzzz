package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void testConstructor()
    {
        var activity = new Activity("1","/path/to/image/","Activity", 200);
        var question = new Question(activity);
        assertNotNull(activity);
        assertEquals(activity, question.activity);
    }

    @Test
    void displayQuestion() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var question = new Question(activity);
        assertEquals("How much energy does taking a shower for 2 minutes take?", question.displayQuestion());

    }

    @Test
    void testEquals() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var question = new Question(activity);
        var question2 = new Question(activity);
        assertEquals(question, question2);
    }

    @Test
    void testEquals2() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var activity2 = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var question = new Question(activity);
        var question2 = new Question(activity2);
        assertEquals(question, question2);
    }

    @Test
    void testNotEquals() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var activity2 = new Activity("1","/path/to/image/","Taking a shower for 5 minutes", 200);
        var question = new Question(activity);
        var question2 = new Question(activity2);
        assertNotEquals(question, question2);
    }

    @Test
    void testHashCode() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var activity2 = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var question = new Question(activity);
        var question2 = new Question(activity2);
        assertEquals(question, question2);
        assertEquals(question.hashCode(), question2.hashCode());
    }

    @Test
    void testToString() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var question = new Question(activity);
        assertEquals(ToStringBuilder.reflectionToString(question, MULTI_LINE_STYLE), question.toString());
    }
}