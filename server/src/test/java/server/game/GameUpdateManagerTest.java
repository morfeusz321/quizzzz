package server.game;

import commons.Player;
import commons.gameupdate.GameUpdateGameStarting;
import commons.gameupdate.GameUpdatePlayerJoined;
import commons.gameupdate.GameUpdatePlayerLeft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameUpdateManagerTest {

    private GameUpdateManager gameUpdateManager;

    private FakeSimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    public void setup() {

        this.simpMessagingTemplate = new FakeSimpMessagingTemplate();
        this.gameUpdateManager = new GameUpdateManager(this.simpMessagingTemplate);

    }

    @Test
    public void testPlayerJoined() {

        Player player = new Player("P1");
        UUID uuid = UUID.randomUUID();
        gameUpdateManager.playerJoined(player, uuid);

        assertEquals(simpMessagingTemplate.getMostRecentSentPayload().getLeft(), "/topic/gameupdates/" + uuid);
        assertEquals(simpMessagingTemplate.getMostRecentSentPayload().getRight(), new GameUpdatePlayerJoined(player));

    }

    @Test
    public void testPlayerLeft() {

        Player player = new Player("P1");
        UUID uuid = UUID.randomUUID();
        gameUpdateManager.playerLeft(player, uuid);

        assertEquals(simpMessagingTemplate.getMostRecentSentPayload().getLeft(), "/topic/gameupdates/" + uuid);
        assertEquals(simpMessagingTemplate.getMostRecentSentPayload().getRight(), new GameUpdatePlayerLeft(player));

    }

    @Test
    public void testStartGame() {

        UUID uuid = UUID.randomUUID();
        gameUpdateManager.startGame(uuid);

        assertEquals(simpMessagingTemplate.getMostRecentSentPayload().getLeft(), "/topic/gameupdates/" + uuid);
        assertEquals(simpMessagingTemplate.getMostRecentSentPayload().getRight(), new GameUpdateGameStarting());

    }

}
