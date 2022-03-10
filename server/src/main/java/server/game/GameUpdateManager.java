package server.game;

import commons.Player;
import commons.gameupdate.GameUpdateGameStarting;
import commons.gameupdate.GameUpdatePlayerJoined;
import commons.gameupdate.GameUpdatePlayerLeft;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameUpdateManager {

    private SimpMessagingTemplate simpMessagingTemplate;

    public GameUpdateManager(SimpMessagingTemplate simpMessagingTemplate) {

        this.simpMessagingTemplate = simpMessagingTemplate;

    }

    public void playerJoined(Player player, UUID gameUUID) {

        this.simpMessagingTemplate.convertAndSend("/topic/gameupdates/" + gameUUID.toString(), new GameUpdatePlayerJoined(player));

    }

    public void playerLeft(Player player, UUID gameUUID) {

        this.simpMessagingTemplate.convertAndSend("/topic/gameupdates/" + gameUUID.toString(), new GameUpdatePlayerLeft(player));

    }

    public void startGame(UUID gameUUID) {

        this.simpMessagingTemplate.convertAndSend("/topic/gameupdates/" + gameUUID.toString(), new GameUpdateGameStarting());

    }

}
