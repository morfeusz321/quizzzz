package server.api;

import commons.Question;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.QuestionDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * The TestQuestionDB is used for injection instead of the QuestionDB while testing
 */
public class TestQuestionDB implements QuestionDB {

    private List<Question> db;

    /**
     * Creates a TestQuestionDB
     */
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
        for(Question q : db) {
            if(q.questionId.equals(uuid)) {
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
