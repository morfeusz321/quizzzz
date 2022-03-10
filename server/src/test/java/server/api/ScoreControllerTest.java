package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.*;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreControllerTest {
    private ScoreController scoreController;

    @BeforeEach
    void setup() {
        ScoreDBController scoreDBController = new ScoreDBController(new TestScoreDB());
        scoreController = new ScoreController(scoreDBController);
    }

    @Test
    public void addScoreTestMalformed() {
        ResponseEntity<Score> s = scoreController.addScore("username", "totally a number");

        assertEquals(HttpStatus.BAD_REQUEST, s.getStatusCode());
    }

    @Test
    public void addScoreSimpleTest() {
        ResponseEntity<Score> s = scoreController.addScore("username", "10");

        assertEquals(HttpStatus.OK, s.getStatusCode());
        assertEquals(new Score("username", 10), s.getBody());
    }

    @Test
    public void addScoreTestOverwrite() {
        ResponseEntity<Score> oldS = scoreController.addScore("username", "10");
        ResponseEntity<Score> newS = scoreController.addScore("username", "100");

        assertEquals(HttpStatus.OK, oldS.getStatusCode());
        assertEquals(HttpStatus.OK, newS.getStatusCode());
        assertEquals(new Score("username", 10), oldS.getBody());
        assertEquals(new Score("username", 100), newS.getBody());
    }

    @Test
    public void addScoreTestNoOverwrite() {
        ResponseEntity<Score> oldS = scoreController.addScore("username", "100");
        ResponseEntity<Score> newS = scoreController.addScore("username", "10");

        assertEquals(HttpStatus.OK, oldS.getStatusCode());
        assertEquals(HttpStatus.OK, newS.getStatusCode());
        assertEquals(new Score("username", 100), oldS.getBody());
        assertEquals(new Score("username", 100), newS.getBody());
    }

    @Test
    public void getAllTestEmpty() {
        assertEquals(new ArrayList<>(), scoreController.getAll());
    }

    @Test
    public void getAllTest() {
        scoreController.addScore("username", "100");
        assertEquals(List.of(new Score("username", 100)), scoreController.getAll());
    }

    private static class TestScoreDB implements ScoreDB {
        private final List<Score> db;

        public TestScoreDB() {
            this.db = new ArrayList<>();
        }

        @Override
        public List<Score> findAll() {
            return db;
        }

        @Override
        public List<Score> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<Score> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public List<Score> findAllById(Iterable<String> strings) {
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
        public void delete(Score entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends String> strings) {

        }

        @Override
        public void deleteAll(Iterable<? extends Score> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public <S extends Score> S save(S entity) {
            db.add(entity);
            return entity;
        }

        @Override
        public <S extends Score> List<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public Optional<Score> findById(String s) {
            return db.stream().filter(score -> score.username.equals(s)).findAny();
        }

        @Override
        public boolean existsById(String s) {
            return false;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends Score> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends Score> List<S> saveAllAndFlush(Iterable<S> entities) {
            return null;
        }

        @Override
        public void deleteAllInBatch(Iterable<Score> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<String> strings) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Score getOne(String s) {
            return null;
        }

        @Override
        public Score getById(String s) {
            return null;
        }

        @Override
        public <S extends Score> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Score> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends Score> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public <S extends Score> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Score> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Score> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Score, R> R findBy(Example<S> example,
                                             Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }
    }
}