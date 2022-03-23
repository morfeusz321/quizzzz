package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.ActivityDBController;
import server.database.QuestionDBController;
import server.game.QuestionGenerator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionGeneratorTest {

    private ActivityDBController activityDBController;
    private QuestionDBController questionDBController;
    private QuestionGenerator questionGenerator;

    @BeforeEach
    public void setup() {

        activityDBController = new ActivityDBController(new TestActivityDB());
        questionDBController = new QuestionDBController(new TestQuestionDB());
        questionGenerator = new QuestionGenerator(new Random(), activityDBController, questionDBController);

    }

    @Test
    public void getWhichIsMoreQuestionTest() {

        activityDBController.getInternalDB().deleteAll();
        Activity activity1 = new Activity("1", "/path/to/image/", "Activity 1", 201);
        Activity activity2 = new Activity("2", "/path/to/image/", "Activity 2", 260);
        Activity activity3 = new Activity("3", "/path/to/image/", "Activity 3", 187);
        activityDBController.getInternalDB().save(activity1);
        activityDBController.getInternalDB().save(activity2);
        activityDBController.getInternalDB().save(activity3);

        Question q = questionGenerator.getWhichIsMoreQuestion();

        assertNotNull(q);
        assertEquals(q, questionDBController.getById(q.questionId));
        assertEquals(activity2.title, q.answerOptions.get((int) q.answer));

    }

    @Test
    public void getRandomQuestionTestNoActivities() {

        Question q = questionGenerator.getRandomQuestion();

        assertNull(q);

    }

    @Test
    public void getRandomQuestionTest() {

        activityDBController.getInternalDB().deleteAll();
        activityDBController.getInternalDB().save(new Activity("id", "imagePath", "title", 0));

        Question q = questionGenerator.getGeneralQuestion();

        assertNotNull(q);
        assertEquals(q, questionDBController.getById(q.questionId));

    }

    @Test
    public void getWhichIsMoreQuestionNoActivities() {

        activityDBController.getInternalDB().deleteAll();
        Question q = questionGenerator.getWhichIsMoreQuestion();

        assertNull(q);

    }

    @Test
    public void getComparisonQuestionTest() {

        activityDBController.getInternalDB().deleteAll();
        Activity activity1 = new Activity("1", "/path/to/image/", "Activity 1", 201);
        Activity activity2 = new Activity("2", "/path/to/image/", "Activity 2", 260);
        Activity activity3 = new Activity("3", "/path/to/image/", "Activity 3", 187);
        Activity activity4 = new Activity("4", "/path/to/image/", "Activity 4", 2070);
        Activity activity5 = new Activity("5", "/path/to/image/", "Activity 5", 20092);
        activityDBController.getInternalDB().save(activity1);
        activityDBController.getInternalDB().save(activity2);
        activityDBController.getInternalDB().save(activity3);
        activityDBController.getInternalDB().save(activity4);
        activityDBController.getInternalDB().save(activity5);

        Question q = questionGenerator.getComparisonQuestion();

        assertNotNull(q);
        assertEquals(q, questionDBController.getById(q.questionId));

        assertTrue(activity1.title.equals(q.answerOptions.get((int) q.answer))
                        ||
                activity3.title.equals(q.answerOptions.get((int) q.answer)));

    }

    @Test
    public void getComparisonNoActivities() {
        activityDBController.getInternalDB().deleteAll();
        Question q = questionGenerator.getComparisonQuestion();

        assertNull(q);
    }

    @Test
    public void getEstimationQuestionTest() {

        activityDBController.getInternalDB().deleteAll();
        activityDBController.getInternalDB().save(new Activity("id", "imagePath", "title", 0));

        Question q = questionGenerator.getEstimationQuestion();

        assertNotNull(q);
        assertEquals(q, questionDBController.getById(q.questionId));

    }

    @Test
    public void getEstimationNoActivities() {

        activityDBController.getInternalDB().deleteAll();
        Question q = questionGenerator.getEstimationQuestion();

        assertNull(q);

    }

    @Test
    public void getComparisonWithLongTest() {

        activityDBController.getInternalDB().deleteAll();
        Activity activity1 = new Activity("1", "/path/to/image/", "Activity 1", 9999999999L);
        Activity activity2 = new Activity("2", "/path/to/image/", "Activity 2", 19999999999L);
        Activity activity3 = new Activity("3", "/path/to/image/", "Activity 3", 9999999998L);
        Activity activity4 = new Activity("4", "/path/to/image/", "Activity 4", 8888888888L);
        Activity activity5 = new Activity("5", "/path/to/image/", "Activity 5", 9999999999999L);
        activityDBController.getInternalDB().save(activity1);
        activityDBController.getInternalDB().save(activity2);
        activityDBController.getInternalDB().save(activity3);
        activityDBController.getInternalDB().save(activity4);
        activityDBController.getInternalDB().save(activity5);

        Question q = questionGenerator.getComparisonQuestion();

        assertNotNull(q);
        assertEquals(q, questionDBController.getById(q.questionId));

        assertTrue(activity1.title.equals(q.answerOptions.get((int) q.answer))
                ||
                activity3.title.equals(q.answerOptions.get((int) q.answer)));

    }

    @Test
    public void getRandomQuestionWithLongTest() {

        activityDBController.getInternalDB().deleteAll();
        activityDBController.getInternalDB().save(new Activity("id1", "imagePath", "title", 9999999999L));
        activityDBController.getInternalDB().save(new Activity("id2", "imagePath", "title", 9999999999L));
        activityDBController.getInternalDB().save(new Activity("id3", "imagePath", "title", 9999999999L));
        activityDBController.getInternalDB().save(new Activity("id4", "imagePath", "title", 9999999999L));
        activityDBController.getInternalDB().save(new Activity("id5", "imagePath", "title", 9999999999L));

        Question q = questionGenerator.getRandomQuestion();

        assertNotNull(q);
        assertEquals(q, questionDBController.getById(q.questionId));

    }

    @Test
    public void getEstimationWithLongTest() {

        activityDBController.getInternalDB().deleteAll();
        activityDBController.getInternalDB().save(new Activity("id", "imagePath", "title", 9999999995L));

        Question q = questionGenerator.getEstimationQuestion();

        assertNotNull(q);
        assertEquals(q, questionDBController.getById(q.questionId));

    }

    @Test
    public void getMoreExpensiveWithLongTest() {

        activityDBController.getInternalDB().deleteAll();
        Activity activity1 = new Activity("1", "/path/to/image/", "Activity 1", 9999999995L);
        Activity activity2 = new Activity("2", "/path/to/image/", "Activity 2", 9999999999L);
        Activity activity3 = new Activity("3", "/path/to/image/", "Activity 3", 9999999990L);
        activityDBController.getInternalDB().save(activity1);
        activityDBController.getInternalDB().save(activity2);
        activityDBController.getInternalDB().save(activity3);

        Question moreExpensive = questionGenerator.getWhichIsMoreQuestion();

        assertNotNull(moreExpensive);
        assertEquals(moreExpensive, questionDBController.getById(moreExpensive.questionId));
        assertEquals(activity2.title, moreExpensive.answerOptions.get((int) moreExpensive.answer));
        // This can be cast to an int because it is only the index, so not a long value.

    }

    // TODO: the following section is commented out so that we still have a reference for testing receiving answers. As
    //  soon as receiving answers is implemented, we should remove this.

    /*
    @Test
    public void answerTestMalformedAnswer() {

        ResponseEntity<AnswerResponseEntity> s = questionController.answer(UUID.randomUUID().toString(), "cheese");

        assertEquals(HttpStatus.BAD_REQUEST, s.getStatusCode());

    }

    @Test
    public void answerTestMalformedUUID() {

        ResponseEntity<AnswerResponseEntity> s = questionController.answer("whatever", "0");

        assertEquals(HttpStatus.BAD_REQUEST, s.getStatusCode());

    }

    @Test
    public void answerTestQuestionDoesNotExist() {

        ResponseEntity<AnswerResponseEntity> s = questionController.answer(UUID.randomUUID().toString(), "0");

        assertEquals(HttpStatus.NO_CONTENT, s.getStatusCode());

    }

    @Test
    public void answerTestGeneralQuestionAnswerCorrect() {

        Activity testActivity = new Activity("id", "imagePath", "title", 0);

        Question testQuestion = new GeneralQuestion(testActivity, List.of("0 Wh", "1 Wh", "2 Wh"), 1);
        questionDBController.add(testQuestion);

        ResponseEntity<AnswerResponseEntity> s = questionController.answer(testQuestion.questionId.toString(), "1");

        assertEquals(HttpStatus.OK, s.getStatusCode());
        assertEquals(new AnswerResponseEntity(true), s.getBody());

    }

    @Test
    public void answerTestGeneralQuestionAnswerIncorrect() {

        Activity testActivity = new Activity("id", "imagePath", "title", 0);

        Question testQuestion = new GeneralQuestion(testActivity, List.of("0 Wh", "1 Wh", "2 Wh"), 1);
        questionDBController.add(testQuestion);

        ResponseEntity<AnswerResponseEntity> s = questionController.answer(testQuestion.questionId.toString(), "2");

        assertEquals(HttpStatus.OK, s.getStatusCode());
        assertEquals(new AnswerResponseEntity(false), s.getBody());

    }

    @Test
    public void answerTestComparisonQuestionCorrect() {

        Activity testActivity = new Activity("id", "imagePath", "title", 5);
        Activity answer1 = new Activity("id1", "imagePath", "title", 5);
        Activity answer2 = new Activity("id2", "imagePath", "title", 10);
        Activity answer3 = new Activity("id3", "imagePath", "title", 20);

        Question testQuestion = new ComparisonQuestion(testActivity, List.of(answer1, answer2, answer3), 1);
        questionDBController.add(testQuestion);

        ResponseEntity<AnswerResponseEntity> s = questionController.answer(testQuestion.questionId.toString(), "1");

        assertEquals(HttpStatus.OK, s.getStatusCode());
        assertEquals(new AnswerResponseEntity(true), s.getBody());

    }

    @Test
    public void answerTestComparisonQuestionIncorrect() {

        Activity testActivity = new Activity("id", "imagePath", "title", 5);
        Activity answer1 = new Activity("id1", "imagePath", "title", 5);
        Activity answer2 = new Activity("id2", "imagePath", "title", 10);
        Activity answer3 = new Activity("id3", "imagePath", "title", 20);

        Question testQuestion = new ComparisonQuestion(testActivity, List.of(answer1, answer2, answer3), 1);
        questionDBController.add(testQuestion);

        ResponseEntity<AnswerResponseEntity> s = questionController.answer(testQuestion.questionId.toString(), "3");

        assertEquals(HttpStatus.OK, s.getStatusCode());
        assertEquals(new AnswerResponseEntity(false), s.getBody());

    }

    @Test
    public void answerTestEstimationQuestionCorrect() {

        Activity testActivity = new Activity("id", "imagePath", "title", 50);

        Question testQuestion = new EstimationQuestion(testActivity);
        questionDBController.add(testQuestion);

        ResponseEntity<AnswerResponseEntity> s = questionController.answer(testQuestion.questionId.toString(), "50");

        assertEquals(HttpStatus.OK, s.getStatusCode());
        assertEquals(new AnswerResponseEntity(true, 0), s.getBody());

    }

    @Test
    public void answerTestEstimationQuestionIncorrect() {

        Activity testActivity = new Activity("id", "imagePath", "title", 50);

        Question testQuestion = new EstimationQuestion(testActivity);
        questionDBController.add(testQuestion);

        ResponseEntity<AnswerResponseEntity> s = questionController.answer(testQuestion.questionId.toString(), "40");
        assertEquals(HttpStatus.OK, s.getStatusCode());
        assertEquals(new AnswerResponseEntity(false, -10), s.getBody());

        s = questionController.answer(testQuestion.questionId.toString(), "60");
        assertEquals(HttpStatus.OK, s.getStatusCode());
        assertEquals(new AnswerResponseEntity(false, 10), s.getBody());

    }
    */

}
