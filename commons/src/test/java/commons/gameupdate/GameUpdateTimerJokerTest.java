package commons.gameupdate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;


class GameUpdateTimerJokerTest {

    @Test
    void testConstructor() {
        ConcurrentHashMap<String, Long> temp = new ConcurrentHashMap<>();
        GameUpdateTimerJoker gameUpdateTimerJoker = new GameUpdateTimerJoker(temp);
        Assertions.assertNotNull(gameUpdateTimerJoker);
    }

    @Test
    void getTime() {
        ConcurrentHashMap<String, Long> temp = new ConcurrentHashMap<>();
        GameUpdateTimerJoker gameUpdateTimerJoker = new GameUpdateTimerJoker(temp);
        Assertions.assertEquals(gameUpdateTimerJoker.getTime(), temp);
    }

}