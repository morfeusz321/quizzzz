package server.game;

import commons.gameupdate.GameUpdate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Manages all incoming WebSocket messages for the game
 */
@Controller
public class GameUpdateIncomingController {

    private GameController gameController;

    /**
     * Instantiates this controller
     * @param gameController the game controller of the application
     */
    public GameUpdateIncomingController(GameController gameController) {

        this.gameController = gameController;

    }

    /**
     * Starts the current game
     * @param s can be anything; sending any WebSocket message to this URL is enough to start
     *          the current game
     */
    @MessageMapping("/start")
    public void startGame(String s) {

        gameController.startCurrentGame();

    }

    /**
     * Sends the emoji update to every player in the game
     * @param gameUpdate game update containing username and sent emoji
     * @param gameId id of the game that sent the update
     * @return
     */
    @MessageMapping("/emoji/{gameId}")
    @SendTo("/topic/gameupdates/{gameId}")
    public GameUpdate sendEmoji(GameUpdate gameUpdate, @DestinationVariable String gameId){
        return gameUpdate;
    }

}
