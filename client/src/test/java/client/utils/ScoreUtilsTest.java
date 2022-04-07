package client.utils;

import commons.AnswerResponseEntity;
import commons.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreUtilsTest {

    private ScoreUtils scoreUtils;
    private Player player;

    @BeforeEach
    void setup() {
        scoreUtils = new ScoreUtils();
        player = new Player();
    }

    @Test
    void constructor() {
        assertNotNull(scoreUtils);
    }

    @Test
    void setScore() {
        scoreUtils.setPlayer(player);
        scoreUtils.setScore(new AnswerResponseEntity(true, 0));
        assertEquals(0, scoreUtils.getPoints());
    }

    @Test
    void setPlayer() {
        scoreUtils.setPlayer(player);
    }

}