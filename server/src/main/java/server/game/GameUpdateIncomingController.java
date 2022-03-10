package server.game;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameUpdateIncomingController {

    private GameController gameController;

    public GameUpdateIncomingController(GameController gameController) {

        this.gameController = gameController;

    }

    @MessageMapping("/start")
    public void startGame(String s) {

        gameController.startCurrentGame();

    }

}
