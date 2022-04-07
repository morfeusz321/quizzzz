package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreControllerTest {

    private ScoreController scoreController;

    @BeforeEach
    void setup() {
        ScoreDBController scoreDBController = new ScoreDBController(new TestScoreDB());
        scoreController = new ScoreController(scoreDBController);
    }

    @Test
    public void addScoreSimpleTest() {
        Score s = scoreController.addScore("username", 10);
        assertEquals(new Score("username", 10), s);
    }

    @Test
    public void addScoreTestOverwrite() {
        Score oldS = scoreController.addScore("username", 10);
        Score newS = scoreController.addScore("username", 100);

        assertEquals(new Score("username", 10), oldS);
        assertEquals(new Score("username", 100), newS);
    }

    @Test
    public void addScoreTestNoOverwrite() {
        Score oldS = scoreController.addScore("username", 100);
        Score newS = scoreController.addScore("username", 10);

        assertEquals(new Score("username", 100), oldS);
        assertEquals(new Score("username", 100), newS);
    }

    @Test
    public void getAllTestEmpty() {
        assertEquals(new ArrayList<>(), scoreController.getAll());
    }

    @Test
    public void getAllTest() {
        scoreController.addScore("username", 100);
        assertEquals(List.of(new Score("username", 100)), scoreController.getAll());
    }

}