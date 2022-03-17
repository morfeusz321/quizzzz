package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.ActivityDBController;
import server.database.QuestionDBController;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionControllerTest {

    private ActivityDBController activityDBController;
    private QuestionDBController questionDBController;
    private QuestionController questionController;

    @BeforeEach
    public void setup() {

        activityDBController = new ActivityDBController(new TestActivityDB());
        questionDBController = new QuestionDBController(new TestQuestionDB());
        questionController = new QuestionController(new Random(), activityDBController, questionDBController);

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

        ResponseEntity<Question> q = questionController.getWhichIsMoreQuestion();

        assertEquals(HttpStatus.OK, q.getStatusCode());
        assertNotNull(q.getBody());
        assertEquals(q.getBody(), questionDBController.getById(q.getBody().questionId));
        assertEquals(activity2.title, q.getBody().answerOptions.get((int) q.getBody().answer));

    }

    @Test
    public void getRandomQuestionTestNoActivities() {

        ResponseEntity<Question> q = questionController.getRandomQuestion();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, q.getStatusCode());

    }

    @Test
    public void getRandomQuestionTest() {

        activityDBController.getInternalDB().deleteAll();
        activityDBController.getInternalDB().save(new Activity("id", "imagePath", "title", 0));

        ResponseEntity<Question> q = questionController.getRandomQuestion();

        assertEquals(HttpStatus.OK, q.getStatusCode());
        assertNotNull(q.getBody());
        assertEquals(q.getBody(), questionDBController.getById(q.getBody().questionId));

    }

    @Test
    public void getWhichIsMoreQuestionNoActivities() {

        activityDBController.getInternalDB().deleteAll();
        ResponseEntity<Question> q = questionController.getWhichIsMoreQuestion();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, q.getStatusCode());

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

        ResponseEntity<Question> q = questionController.getComparisonQuestion();

        assertEquals(HttpStatus.OK, q.getStatusCode());
        assertNotNull(q.getBody());
        assertEquals(q.getBody(), questionDBController.getById(q.getBody().questionId));

        assertTrue(activity1.title.equals(q.getBody().answerOptions.get((int) q.getBody().answer))
                        ||
                activity3.title.equals(q.getBody().answerOptions.get((int) q.getBody().answer)));

    }

    @Test
    public void getComparisonNoActivities() {
        activityDBController.getInternalDB().deleteAll();
        ResponseEntity<Question> q = questionController.getComparisonQuestion();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, q.getStatusCode());
    }

    @Test
    public void getEstimationQuestionTest() {

        activityDBController.getInternalDB().deleteAll();
        activityDBController.getInternalDB().save(new Activity("id", "imagePath", "title", 0));

        ResponseEntity<Question> q = questionController.getEstimationQuestion();

        assertEquals(HttpStatus.OK, q.getStatusCode());
        assertNotNull(q.getBody());
        assertEquals(q.getBody(), questionDBController.getById(q.getBody().questionId));

    }

    @Test
    public void getEstimationNoActivities() {

        activityDBController.getInternalDB().deleteAll();
        ResponseEntity<Question> q = questionController.getEstimationQuestion();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, q.getStatusCode());

    }


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
}
