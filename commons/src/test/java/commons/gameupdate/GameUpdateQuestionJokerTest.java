package commons.gameupdate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class GameUpdateQuestionJokerTest {

    @Test
    void testConstructor(){
        GameUpdateQuestionJoker gameUpdateQuestionJoker = new GameUpdateQuestionJoker(1);
        Assertions.assertNotNull(gameUpdateQuestionJoker);
    }

    @Test
    void getButtonNumber() {
        GameUpdateQuestionJoker gameUpdateQuestionJoker = new GameUpdateQuestionJoker(1);
        Assertions.assertEquals(gameUpdateQuestionJoker.getButtonNumber(),1);
    }
}