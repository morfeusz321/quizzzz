package server.game;

import commons.Player;
import commons.gameupdate.GameUpdateGameStarting;
import commons.gameupdate.GameUpdateNoQuestions;
import commons.gameupdate.GameUpdatePlayerJoined;
import commons.gameupdate.GameUpdatePlayerLeft;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Manages all outgoing WebSocket messages for the game updates
 */
@Component
public class GameUpdateManager {

    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Instantiates this controller
     *
     * @param simpMessagingTemplate the messaging template for sending messages
     */
    public GameUpdateManager(SimpMessagingTemplate simpMessagingTemplate) {

        this.simpMessagingTemplate = simpMessagingTemplate;

    }

    /**
     * Sends a message to all subscribers of the game updates topic of the game with the specified UUID
     * that the specified player has joined
     *
     * @param player   the player that joined
     * @param gameUUID the UUID of the game that the player has joined
     */
    public void playerJoined(Player player, UUID gameUUID) {

        this.simpMessagingTemplate.convertAndSend("/topic/gameupdates/" + gameUUID.toString(), new GameUpdatePlayerJoined(player));

    }

    /**
     * Sends a message to all subscribers of the game updates topic of the game with the specified UUID
     * that the specified player has left
     *
     * @param player   the player that left
     * @param gameUUID the UUID of the game that the player has left
     */
    public void playerLeft(Player player, UUID gameUUID) {

        this.simpMessagingTemplate.convertAndSend("/topic/gameupdates/" + gameUUID.toString(), new GameUpdatePlayerLeft(player));

    }

    /**
     * Sends a message to all subscribers of the game updates topic of the game with the specified UUID
     * that the game is starting
     *
     * @param gameUUID the UUID of the game that is starting
     */
    public void startGame(UUID gameUUID) {

        this.simpMessagingTemplate.convertAndSend("/topic/gameupdates/" + gameUUID.toString(), new GameUpdateGameStarting());

    }

    /**
     * Sends a message to all subscribers of the game updates topic of the game with the specified UUID
     * that no questions could be generated for this game.
     *
     * @param gameUUID the UUID of the game that could not be started
     */
    public void noQuestionsGenerated(UUID gameUUID) {

        this.simpMessagingTemplate.convertAndSend("/topic/gameupdates/" + gameUUID.toString(), new GameUpdateNoQuestions());

    }

}
