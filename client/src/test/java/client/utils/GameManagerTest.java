package client.utils;

import commons.Activity;
import commons.GeneralQuestion;
import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {

    private GameManager manager;
    private Activity activity;
    private Question q;
    private List<Question> list;

    @BeforeEach
    void setup(){
        this.manager = new GameManager();
        this.activity = new Activity();
        this.list = new ArrayList<>();
        this.q = new GeneralQuestion(activity, new ArrayList<String>(3), 0);
    }

    @Test
    void constructorTests(){
        assertNotNull(this.manager);
    }

    @Test
    void getQuestionsNull() {
        assertNull(manager.getQuestions());
    }

    @Test
    void getQuestions() {
        list.add(q);
        manager.setQuestions(list);
        List<Question> result = new ArrayList<>();
        result.add(q);
        assertEquals(result, manager.getQuestions());

    }

    @Test
    void setQuestions() {
        list.add(q);
        manager.setQuestions(list);
        assertNotNull(manager.getQuestions());
    }

    @Test
    void getCurrentQuestion() {
        list.add(q);
        manager.setQuestions(list);
        manager.setCurrentQuestionByIdx(0);
        assertEquals(q, manager.getCurrentQuestion());
    }

    @Test
    void setCurrentQuestionByIdx() {
        list.add(q);
        manager.setQuestions(list);
        manager.setCurrentQuestionByIdx(0);
        assertNotNull(manager.getCurrentQuestion());
    }
}