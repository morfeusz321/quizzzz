package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.ActivityDB;
import server.database.ActivityDBController;
import server.database.QuestionDB;
import server.database.QuestionDBController;

import java.util.*;
import java.util.function.Function;

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

//    @Test
//    public void getWhichIsMoreQuestionTest() {
//
//        activityDBController.forceReload();
//
//        ResponseEntity<Question> q = questionController.getWhichIsMoreQuestion();
//
//        assertEquals(HttpStatus.OK, q.getStatusCode());
//        assertNotNull(q.getBody());
//        assertEquals(q.getBody(), questionDBController.getById(q.getBody().questionId));
//
//    }

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

        ResponseEntity<Question> q = questionController.getWhichIsMoreQuestion();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, q.getStatusCode());

    }

    @Test
    public void getComparisonQuestionTest() {

        activityDBController.getInternalDB().deleteAll();
        activityDBController.getInternalDB().save(new Activity("id1", "imagePath", "title", 0));
        activityDBController.getInternalDB().save(new Activity("id2", "imagePath", "title", 0));
        activityDBController.getInternalDB().save(new Activity("id3", "imagePath", "title", 0));
        activityDBController.getInternalDB().save(new Activity("id4", "imagePath", "title", 0));
        activityDBController.getInternalDB().save(new Activity("id5", "imagePath", "title", 0));

        ResponseEntity<Question> q = questionController.getComparisonQuestion();

        assertEquals(HttpStatus.OK, q.getStatusCode());
        assertNotNull(q.getBody());
        assertEquals(q.getBody(), questionDBController.getById(q.getBody().questionId));

    }

//    @Test
//    public void getComparisonNoActivities() {
//        ResponseEntity<Question> q = questionController.getComparisonQuestion();
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, q.getStatusCode());
//    }

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

    private class TestActivityDB implements ActivityDB {

        private List<Activity> db;

        public TestActivityDB() {

            this.db = new ArrayList<>();

        }

        @Override
        public List<Activity> findAll() {
            return db;
        }

        @Override
        public List<Activity> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<Activity> findAll(Pageable pageable) {

            List<Activity> returnValues = new ArrayList<>();
            for (long i = pageable.getOffset(); i < pageable.getOffset() + pageable.getPageSize(); i++) {

                returnValues.add(db.get((int) i));

            }

            return new PageImpl<Activity>(returnValues);

        }

        @Override
        public List<Activity> findAllById(Iterable<String> strings) {
            return null;
        }

        @Override
        public long count() {
            return db.size();
        }

        @Override
        public void deleteById(String s) {

        }

        @Override
        public void delete(Activity entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends String> strings) {

        }

        @Override
        public void deleteAll(Iterable<? extends Activity> entities) {

        }

        @Override
        public void deleteAll() {
            db = new ArrayList<>();
        }

        @Override
        public <S extends Activity> S save(S entity) {
            db.add(entity);
            return entity;
        }

        @Override
        public <S extends Activity> List<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public Optional<Activity> findById(String s) {
            return db.stream().filter(a -> a.id.equals(s)).findAny();
        }

        @Override
        public boolean existsById(String s) {
            return false;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends Activity> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends Activity> List<S> saveAllAndFlush(Iterable<S> entities) {
            return null;
        }

        @Override
        public void deleteAllInBatch(Iterable<Activity> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<String> strings) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Activity getOne(String s) {
            return null;
        }

        @Override
        public Activity getById(String s) {
            return null;
        }

        @Override
        public <S extends Activity> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Activity> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends Activity> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public <S extends Activity> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Activity> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Activity> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Activity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }


        @Override
        public ArrayList<Activity> getFiveRandomActivities() {
            ArrayList<Activity> temp = new ArrayList<>();
            Activity activity1 = new Activity("1", "/path/to/image/", "Activity", 201);
            Activity activity2 = new Activity("2", "/path/to/image/", "Activity", 260);
            Activity activity3 = new Activity("3", "/path/to/image/", "Activity", 20);
            Activity activity4 = new Activity("4", "/path/to/image/", "Activity", 2070);
            Activity activity5 = new Activity("5", "/path/to/image/", "Activity", 20092);
            temp.add(activity1);
            temp.add(activity2);
            temp.add(activity3);
            temp.add(activity4);
            temp.add(activity5);
            return temp;
        }

        @Override
        public ArrayList<Activity> getThreeRandomActivities() {
            return null;
        }

    }

    private class TestQuestionDB implements QuestionDB {

        private List<Question> db;

        public TestQuestionDB() {

            this.db = new ArrayList<>();

        }

        @Override
        public List<Question> findAll() {
            return db;
        }

        @Override
        public List<Question> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<Question> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public List<Question> findAllById(Iterable<UUID> uuids) {
            return null;
        }

        @Override
        public long count() {
            return db.size();
        }

        @Override
        public void deleteById(UUID uuid) {
            List<Question> toRemove = new ArrayList<>();
            for (Question q : db) {
                if (q.questionId.equals(uuid)) {
                    toRemove.add(q);
                }
            }
            db.removeAll(toRemove);
        }

        @Override
        public void delete(Question entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends UUID> uuids) {

        }

        @Override
        public void deleteAll(Iterable<? extends Question> entities) {

        }

        @Override
        public void deleteAll() {
            db = new ArrayList<>();
        }

        @Override
        public <S extends Question> S save(S entity) {
            db.add(entity);
            return entity;
        }

        @Override
        public <S extends Question> List<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public Optional<Question> findById(UUID uuid) {
            return db.stream().filter(q -> q.questionId.equals(uuid)).findAny();
        }

        @Override
        public boolean existsById(UUID uuid) {
            return false;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends Question> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends Question> List<S> saveAllAndFlush(Iterable<S> entities) {
            return null;
        }

        @Override
        public void deleteAllInBatch(Iterable<Question> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Question getOne(UUID uuid) {
            return null;
        }

        @Override
        public Question getById(UUID uuid) {
            return null;
        }

        @Override
        public <S extends Question> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Question> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends Question> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public <S extends Question> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Question> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Question> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Question, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }
    }

}
