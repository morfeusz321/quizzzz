package server.api;

import commons.Activity;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ActivityDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

/**
 * The TestActivityDB is used for injection instead of the ActivityDB while testing
 */
public class TestActivityDB implements ActivityDB {

    private List<Activity> db;

    /**
     * Creates a TestActivityDB
     */
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

        ArrayList<Activity> random = new ArrayList<>();
        Random r = new Random();

        List<Activity> copy = new ArrayList<>();
        copy.addAll(db);

        for(int i = 0; i < 5 && copy.size() > 0; i++) {

            int idx = r.nextInt(copy.size());
            random.add(copy.get(idx));
            copy.remove(idx);

        }

        return random;
    }

    @Override
    public ArrayList<Activity> getThreeRandomActivities() {

        ArrayList<Activity> random = new ArrayList<>();
        Random r = new Random();

        List<Activity> copy = new ArrayList<>();
        copy.addAll(db);

        for(int i = 0; i < 3 && copy.size() > 0; i++) {

            int idx = r.nextInt(copy.size());
            random.add(copy.get(idx));
            copy.remove(idx);

        }

        return random;

    }

}
