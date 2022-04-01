package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.ActivityDBController;
import server.database.QuestionDBController;
import server.game.GameTestUtils;
import server.game.questions.QuestionGenerator;
import server.game.questions.QuestionGeneratorUtils;

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
        QuestionGeneratorUtils utils = new QuestionGeneratorUtils();
        questionGenerator = new QuestionGenerator(
                new Random(),
                activityDBController,
                questionDBController,
                utils);

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
        assertEquals(activity2.title, q.answerOptions.get((int) q.answer-1));

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
        Activity activity1 = new Activity("1", "/path/to/image/", "Activity 1", 100);
        Activity activity2 = new Activity("2", "/path/to/image/", "Activity 2", 65);
        Activity activity3 = new Activity("3", "/path/to/image/", "Activity 3", 135);
        Activity activity4 = new Activity("4", "/path/to/image/", "Activity 4", 101);
        Activity activity5 = new Activity("5", "/path/to/image/", "Activity 5", 99);
        activityDBController.getInternalDB().save(activity1);
        activityDBController.getInternalDB().save(activity2);
        activityDBController.getInternalDB().save(activity3);
        activityDBController.getInternalDB().save(activity4);
        activityDBController.getInternalDB().save(activity5);

        Question q = questionGenerator.getComparisonQuestion();

        assertNotNull(q);
        assertEquals(q, questionDBController.getById(q.questionId));

        // From the given activities, the main one needs to be either 1, 4 or 5 (for others no correct
        // answer sets can be generated)
        assertTrue(q.activityTitle.equals(activity1.title) || q.activityTitle.equals(activity4.title) ||
                q.activityTitle.equals(activity5.title));
        // The other answer options should be activity 2 and 3
        assertTrue(q.answerOptions.contains(activity2.title));
        assertTrue(q.answerOptions.contains(activity3.title));
        // It should not contain the title as answer
        assertFalse(q.answerOptions.contains(q.activityTitle));

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
        Activity activity1 = new Activity("1", "/path/to/image/", "Activity 1", 100000000000L);
        Activity activity2 = new Activity("2", "/path/to/image/", "Activity 2", 65000000000L);
        Activity activity3 = new Activity("3", "/path/to/image/", "Activity 3", 135000000000L);
        Activity activity4 = new Activity("4", "/path/to/image/", "Activity 4", 100000000001L);
        Activity activity5 = new Activity("5", "/path/to/image/", "Activity 5", 99999999999L);
        activityDBController.getInternalDB().save(activity1);
        activityDBController.getInternalDB().save(activity2);
        activityDBController.getInternalDB().save(activity3);
        activityDBController.getInternalDB().save(activity4);

        Question q = questionGenerator.getComparisonQuestion();

        assertNotNull(q);
        assertEquals(q, questionDBController.getById(q.questionId));

        // From the given activities, the main one needs to be either 1, 4 or 5 (for others no correct
        // answer sets can be generated)
        assertTrue(q.activityTitle.equals(activity1.title) || q.activityTitle.equals(activity4.title) ||
                q.activityTitle.equals(activity5.title));
        // The other answer options should be activity 2 and 3
        assertTrue(q.answerOptions.contains(activity2.title));
        assertTrue(q.answerOptions.contains(activity3.title));
        // It should not contain the title as answer
        assertFalse(q.answerOptions.contains(q.activityTitle));

    }

    @Test
    public void getEstimationWithLongTest() {
        // It should return null as long values are above the cut-off point for estimation questions.

        activityDBController.getInternalDB().deleteAll();
        activityDBController.getInternalDB().save(new Activity("id", "imagePath", "title", 9999999995L));

        Question q = questionGenerator.getEstimationQuestion();
        assertNull(q);

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
        assertEquals(activity2.title, moreExpensive.answerOptions.get((int) moreExpensive.answer-1));
        // This can be cast to an int because it is only the index, so not a long value.

    }

    @Test
    public void testMinPerQuestionType() {

        // TODO: this is not the best way to test this, as randomness is involved.

        activityDBController.getInternalDB().deleteAll();
        GameTestUtils utils = new GameTestUtils();
        utils.initActivityDB(activityDBController);

        List<Question> questions = questionGenerator.generateGameQuestions(3);

        // Count the occurrences per question type
        int[] count = new int[4];
        for(Question q : questions) {
            if(q instanceof GeneralQuestion){
                count[0]++;
            } else if(q instanceof ComparisonQuestion){
                count[1]++;
            } else if(q instanceof EstimationQuestion){
                count[2]++;
            } else{
                count[3]++;
            }
        }

        // Check if the number of questions per type are sufficient
        for(int i = 0; i < 4; i++){
            if(count[i] < 3){
                fail();
            }
        }

    }

    @Test
    public void testNoDuplicatesQuestionGeneration() {

        // TODO: this is not the best way to test this, as randomness is involved.

        activityDBController.getInternalDB().deleteAll();
        GameTestUtils utils = new GameTestUtils();
        utils.initActivityDB(activityDBController);

        List<Question> questions = questionGenerator.generateGameQuestions(3);
        Set<Question> setQuestions = new HashSet<>(questions);

        // If the set size and the list size are equal, that means that there are no duplicates.
        assertEquals(20, questions.size());
        assertEquals(20, setQuestions.size());

    }

    @Test
    public void testGenerateGameQuestionsThrows() {

        assertThrows(IllegalArgumentException.class, () -> {
            questionGenerator.generateGameQuestions(6);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            questionGenerator.generateGameQuestions(-1);
        });

    }


    @Test
    public void getMoreExpensiveIsNull() {

        // It should not be possible to generate a WhichIsMore question if the consumptions are identical.
        activityDBController.getInternalDB().deleteAll();
        Activity activity1 = new Activity("1", "/path/to/image/", "Activity 1", 10);
        Activity activity2 = new Activity("2", "/path/to/image/", "Activity 2", 10);
        Activity activity3 = new Activity("3", "/path/to/image/", "Activity 3", 10);
        activityDBController.getInternalDB().save(activity1);
        activityDBController.getInternalDB().save(activity2);
        activityDBController.getInternalDB().save(activity3);

        Question moreExpensive = questionGenerator.getWhichIsMoreQuestion();
        assertNull(moreExpensive);

    }

    @Test
    public void getMoreExpensiveRangeTest() {

        // The question should not contain an "obvious" answer, i.e. 4 as an answer. The consumption is out of the
        // desired range. This is checked in this test.
        // TODO: when Random with seed is added, change this test. This is testing with randomness, so not desired.
        activityDBController.getInternalDB().deleteAll();
        Activity activity1 = new Activity("1", "/path/to/image/", "Activity 1", 9);
        Activity activity2 = new Activity("2", "/path/to/image/", "Activity 2", 10);
        Activity activity3 = new Activity("3", "/path/to/image/", "Activity 3", 11);
        Activity activity4 = new Activity("4", "/path/to/image/", "Activity 4", 999999999);
        activityDBController.getInternalDB().save(activity1);
        activityDBController.getInternalDB().save(activity2);
        activityDBController.getInternalDB().save(activity3);
        activityDBController.getInternalDB().save(activity4);

        Question moreExpensive = questionGenerator.getWhichIsMoreQuestion();
        List<String> allActivities = new ArrayList<>();
        allActivities.add(moreExpensive.activityTitle);
        allActivities.addAll(moreExpensive.answerOptions);

        assertNotNull(moreExpensive);
        assertFalse(allActivities.contains(activity4.title));

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
