package server.api;

import commons.Score;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ScoreDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestScoreDB implements ScoreDB {

    private List<Score> db;

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
        this.db = new ArrayList<>();
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
