package server.api;

import commons.Score;
import commons.gameupdate.GameUpdate;
import commons.gameupdate.GameUpdateFullPlayerList;
import commons.gameupdate.GameUpdateNameInUse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.ScoreDBController;
import server.game.FakeSimpMessagingTemplate;
import server.game.GameController;
import server.game.GameUpdateManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private FakeSimpMessagingTemplate fakeSimpMessagingTemplate;
    private TestGameController testGameController;
    private ScoreDBController scoreDBController;

    private UserController userController;

    @BeforeEach
    public void setup() {

        this.fakeSimpMessagingTemplate = new FakeSimpMessagingTemplate();
        this.testGameController = new TestGameController(this.fakeSimpMessagingTemplate);
        this.scoreDBController = new ScoreDBController(new TestScoreDB());

        this.userController = new UserController(this.testGameController, this.scoreDBController);

    }

    @Test
    public void testJoinGameIllegalGameType() {

        ResponseEntity<GameUpdate> response =  userController.joinGame("Username", "SOME_ILLEGAL_GAME_TYPE", Optional.empty());

        assertFalse(testGameController.getCurrentGame().containsPlayer("Username"));
        assertSame(response.getStatusCode(), HttpStatus.BAD_REQUEST);

    }

    @Test
    public void testJoinGameMultiplayer() {

        ResponseEntity<GameUpdate> response =  userController.joinGame("Username", "MULTIPLAYER", Optional.empty());

        assertTrue(testGameController.getCurrentGame().containsPlayer("Username"));
        assertTrue(response.getBody() instanceof GameUpdateFullPlayerList);
        assertEquals(1, ((GameUpdateFullPlayerList) response.getBody()).getPlayerList().size());
        assertEquals(1, testGameController.getCurrentGame().getPlayers().size());

        response =  userController.joinGame("Someone else", "MULTIPLAYER", Optional.empty());

        assertTrue(testGameController.getCurrentGame().containsPlayer("Username"));
        assertTrue(testGameController.getCurrentGame().containsPlayer("Someone else"));
        assertTrue(response.getBody() instanceof GameUpdateFullPlayerList);
        assertEquals(2, ((GameUpdateFullPlayerList) response.getBody()).getPlayerList().size());
        assertEquals(2, testGameController.getCurrentGame().getPlayers().size());

    }

    @Test
    public void testJoinGameMultiplayerNameInUse() {

        ResponseEntity<GameUpdate> response1 =  userController.joinGame("Username", "MULTIPLAYER", Optional.empty());
        ResponseEntity<GameUpdate> response2 =  userController.joinGame("Username", "MULTIPLAYER", Optional.empty());

        assertTrue(testGameController.getCurrentGame().containsPlayer("Username"));
        assertTrue(response1.getBody() instanceof GameUpdateFullPlayerList);

        assertEquals(1, testGameController.getCurrentGame().getPlayers().size());
        assertTrue(response2.getBody() instanceof GameUpdateNameInUse);

    }

    @Test
    public void testJoinGameSingleplayer() {

        scoreDBController.clear();

        ResponseEntity<GameUpdate> response =  userController.joinGame("Username", "SINGLEPLAYER", Optional.empty());

        assertTrue(response.getBody() instanceof GameUpdateFullPlayerList);

        UUID uuid = ((GameUpdateFullPlayerList) response.getBody()).getGameUUID();
        assertNotNull(testGameController.getGame(uuid));
        assertTrue(testGameController.getGame(uuid).containsPlayer("Username"));
        assertEquals(1, testGameController.getGame(uuid).getPlayers().size());

    }

    @Test
    public void testJoinGameSingleplayerNameInUseNoConfirm() {

        scoreDBController.clear();
        scoreDBController.add(new Score("Username", 9000));

        ResponseEntity<GameUpdate> response =  userController.joinGame("Username", "SINGLEPLAYER", Optional.empty());

        assertTrue(response.getBody() instanceof GameUpdateNameInUse);
        assertFalse(testGameController.currentGameContains("Username"));

    }

    @Test
    public void testJoinGameSingleplayerNameInUseIllegalConfirm() {

        scoreDBController.clear();
        scoreDBController.add(new Score("Username", 9000));

        ResponseEntity<GameUpdate> response =  userController.joinGame("Username", "SINGLEPLAYER", Optional.of("somethingotherthantrue"));

        assertTrue(response.getBody() instanceof GameUpdateNameInUse);
        assertFalse(testGameController.currentGameContains("Username"));

    }

    @Test
    public void testJoinGameSingleplayerNameInUseConfirm() {

        scoreDBController.clear();
        scoreDBController.add(new Score("Username", 9000));

        ResponseEntity<GameUpdate> response =  userController.joinGame("Username", "SINGLEPLAYER", Optional.of("true"));

        assertTrue(response.getBody() instanceof GameUpdateFullPlayerList);

        UUID uuid = ((GameUpdateFullPlayerList) response.getBody()).getGameUUID();
        assertNotNull(testGameController.getGame(uuid));
        assertTrue(testGameController.getGame(uuid).containsPlayer("Username"));
        assertEquals(1, testGameController.getGame(uuid).getPlayers().size());

    }

    @Test
    public void testLeaveGame() {

        ResponseEntity<GameUpdate> response =  userController.joinGame("Username", "MULTIPLAYER", Optional.empty());

        assertTrue(testGameController.getCurrentGame().containsPlayer("Username"));
        assertTrue(response.getBody() instanceof GameUpdateFullPlayerList);
        assertEquals(1, ((GameUpdateFullPlayerList) response.getBody()).getPlayerList().size());
        assertEquals(1, testGameController.getCurrentGame().getPlayers().size());

        ResponseEntity<String> response2 = userController.leaveGame("Username", ((GameUpdateFullPlayerList) response.getBody()).getGameUUID().toString());

        assertFalse(testGameController.getCurrentGame().containsPlayer("Username"));
        assertSame(response2.getStatusCode(), HttpStatus.OK);
        assertEquals(0, testGameController.getCurrentGame().getPlayers().size());

    }

    @Test
    public void testLeaveGameMalformedUUID() {

        ResponseEntity<GameUpdate> response =  userController.joinGame("Username", "MULTIPLAYER", Optional.empty());

        assertTrue(testGameController.getCurrentGame().containsPlayer("Username"));
        assertTrue(response.getBody() instanceof GameUpdateFullPlayerList);
        assertEquals(1, ((GameUpdateFullPlayerList) response.getBody()).getPlayerList().size());
        assertEquals(1, testGameController.getCurrentGame().getPlayers().size());

        ResponseEntity<String> response2 = userController.leaveGame("Username", "this-is-a-valid-uuid-trust-me");

        assertTrue(testGameController.getCurrentGame().containsPlayer("Username"));
        assertSame(response2.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(1, testGameController.getCurrentGame().getPlayers().size());

    }

    @Test
    public void testLeaveGameUserNotInGame() {

        ResponseEntity<GameUpdate> response =  userController.joinGame("Username", "MULTIPLAYER", Optional.empty());

        assertTrue(testGameController.getCurrentGame().containsPlayer("Username"));
        assertTrue(response.getBody() instanceof GameUpdateFullPlayerList);
        assertEquals(1, ((GameUpdateFullPlayerList) response.getBody()).getPlayerList().size());
        assertEquals(1, testGameController.getCurrentGame().getPlayers().size());

        ResponseEntity<String> response2 = userController.leaveGame("Someone else", ((GameUpdateFullPlayerList) response.getBody()).getGameUUID().toString());

        assertTrue(testGameController.getCurrentGame().containsPlayer("Username"));
        assertSame(response2.getStatusCode(), HttpStatus.OK);
        assertEquals(1, testGameController.getCurrentGame().getPlayers().size());

    }

    private class TestGameController extends GameController {

        public TestGameController(FakeSimpMessagingTemplate messagingTemplate) {

            super(new GameUpdateManager(messagingTemplate));

        }

    }

}
