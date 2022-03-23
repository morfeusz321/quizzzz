package server.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameUpdateIncomingControllerTest {

    @Test
    public void testStartGame() {

        FakeGameController gameController = new FakeGameController();

        GameUpdateIncomingController gameUpdateIncomingController = new GameUpdateIncomingController(gameController);
        gameUpdateIncomingController.startGame("");

        assertTrue(gameController.isCurrentGameStarted());

    }

    private class FakeGameController extends GameController {

        private boolean gameStarted;

        public FakeGameController() {

            super(null, null);

            this.gameStarted = false;

        }

        @Override
        public void startCurrentGame() {

            this.gameStarted = true;

        }

        public boolean isCurrentGameStarted() {

            return this.gameStarted;

        }

    }

}
