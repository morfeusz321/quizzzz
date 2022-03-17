package server.game;

import commons.GameType;
import commons.Player;
import commons.gameupdate.GameUpdate;
import commons.gameupdate.GameUpdateFullPlayerList;
import commons.gameupdate.GameUpdateGameStarting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private GameUpdateManager gameUpdateManager;
    private FakeSimpMessagingTemplate simpMessagingTemplate;

    private GameController gameController;

    @BeforeEach
    public void setup() {

        this.simpMessagingTemplate = new FakeSimpMessagingTemplate();
        this.gameUpdateManager = new GameUpdateManager(this.simpMessagingTemplate);

        this.gameController = new GameController(this.gameUpdateManager);
        this.gameController.setApplicationContext(new FakeApplicationContext());
        this.gameController.init();

    }

    @Test
    public void testConstructor() {

        assertNotNull(gameController);
        assertNotNull(gameController.getCurrentGame());

        try {

            Field gamesField = GameController.class.getDeclaredField("startedGames");
            gamesField.setAccessible(true);
            assertNotNull(gamesField.get(gameController));

            Field gameUpdateManagerField = GameController.class.getDeclaredField("gameUpdateManager");
            gameUpdateManagerField.setAccessible(true);
            assertNotNull(gameUpdateManagerField.get(gameController));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail();
        }

    }

    @Test
    public void testCurrentGame() {

        assertNotNull(gameController.getCurrentGame());
        assertNotNull(gameController.getCurrentGamePlayers());
        assertNotNull(gameController.getCurrentGameUUID());

    }

    @Test
    public void testCurrentGameAddPlayer() {

        Player player = new Player("P1");
        assertTrue(gameController.addPlayerToCurrentGame(player));

        assertTrue(gameController.getCurrentGame().containsPlayer(player));

    }

    @Test
    public void testCurrentGameAddPlayerAlreadyInGame() {

        Player player = new Player("P1");
        assertTrue(gameController.addPlayerToCurrentGame(player));
        assertFalse(gameController.addPlayerToCurrentGame(player));
        assertTrue(gameController.getCurrentGame().containsPlayer(player));

    }

    @Test
    public void testCurrentGameContains() {

        Player player = new Player("P1");
        assertTrue(gameController.addPlayerToCurrentGame(player));

        assertTrue(gameController.getCurrentGame().containsPlayer(player));
        assertTrue(gameController.currentGameContains(player));
        assertTrue(gameController.currentGameContains("P1"));

        Player player2 = new Player("P2");
        assertFalse(gameController.getCurrentGame().containsPlayer(player2));
        assertFalse(gameController.currentGameContains(player2));
        assertFalse(gameController.currentGameContains("P2"));

    }

    @Test
    public void testStartCurrentGame() {

        UUID uuid = gameController.getCurrentGameUUID();

        gameController.startCurrentGame();

        assertNotEquals(uuid, gameController.getCurrentGameUUID());
        assertEquals("/topic/gameupdates/" + uuid, simpMessagingTemplate.getMostRecentSentPayload().getLeft());
        assertEquals(new GameUpdateGameStarting(), simpMessagingTemplate.getMostRecentSentPayload().getRight());
        assertNotNull(gameController.getGame(uuid));
        assertEquals(uuid, gameController.getGame(uuid).getUUID());

    }

    @Test
    public void testGetGame() {

        UUID uuid = gameController.getCurrentGameUUID();
        gameController.startCurrentGame();

        UUID uuid2 = gameController.getCurrentGameUUID();

        assertEquals(uuid, gameController.getGame(uuid).getUUID());
        assertEquals(gameController.getCurrentGame(), gameController.getGame(uuid2));
        assertEquals(uuid2, gameController.getGame(uuid2).getUUID());
        assertNull(gameController.getGame(UUID.randomUUID()));

    }

    @Test
    public void testRemovePlayerFromCurrentGame() {

        Player player1 = new Player("P1");

        gameController.addPlayerToCurrentGame(player1);
        assertTrue(gameController.currentGameContains(player1));
        gameController.removePlayerFromCurrentGame(player1);
        assertFalse(gameController.currentGameContains(player1));

        gameController.addPlayerToCurrentGame(player1);
        assertTrue(gameController.currentGameContains(player1));
        gameController.removePlayerFromCurrentGame("P1");
        assertFalse(gameController.currentGameContains(player1));

    }

    @Test
    public void testRemovePlayerFromGame() {

        Player player1 = new Player("P1");

        UUID uuid = gameController.getCurrentGameUUID();
        gameController.startCurrentGame();

        gameController.getGame(uuid).addPlayer(player1);
        assertTrue(gameController.getGame(uuid).containsPlayer(player1));
        gameController.removePlayerFromGame(player1, uuid);
        assertFalse(gameController.getGame(uuid).containsPlayer(player1));

        gameController.getGame(uuid).addPlayer(player1);
        assertTrue(gameController.getGame(uuid).containsPlayer(player1));
        gameController.removePlayerFromGame("P1", uuid);
        assertFalse(gameController.getGame(uuid).containsPlayer(player1));

    }

    @Test
    public void testCreateSinglePlayerGame() {

        Player player1 = new Player("P1");
        GameUpdate gameUpdate = gameController.createSinglePlayerGame(player1);

        assertTrue(gameUpdate instanceof GameUpdateFullPlayerList);

        GameUpdateFullPlayerList gameUpdateFullPlayerList = (GameUpdateFullPlayerList) gameUpdate;
        List<Player> playerList = gameUpdateFullPlayerList.getPlayerList();
        UUID uuid = gameUpdateFullPlayerList.getGameUUID();

        assertNotNull(gameController.getGame(uuid));
        assertNotEquals(gameController.getGame(uuid), gameController.getCurrentGame());
        assertEquals(playerList, gameController.getGame(uuid).getPlayers());
        assertTrue(playerList.contains(player1));
        assertEquals(1, playerList.size());
        assertEquals(GameType.SINGLEPLAYER, gameController.getGame(uuid).getGameType());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals("/topic/gameupdates/" + uuid, simpMessagingTemplate.getMostRecentSentPayload().getLeft());
        assertEquals(new GameUpdateGameStarting(), simpMessagingTemplate.getMostRecentSentPayload().getRight());

    }

}
