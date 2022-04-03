package server.game;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.api.TestActivityDB;
import server.api.TestQuestionDB;
import server.database.ActivityDBController;
import server.database.QuestionDBController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Player player1;
    private Player player2;

    private UUID uuid;
    private Game game;

    private ActivityDBController activityDBController;
    private QuestionDBController questionDBController;
    private QuestionGenerator questionGenerator;
    private CommonUtils utils;

    @BeforeEach
    public void setup() {

        this.player1 = new Player("P1");
        this.player2 = new Player("P2");

        this.uuid = UUID.randomUUID();

        // TODO: not sure if this is the correct way to handle this
        utils = new CommonUtils();
        activityDBController = new ActivityDBController(new TestActivityDB());
        questionDBController = new QuestionDBController(new TestQuestionDB());
        questionGenerator = new QuestionGenerator(new Random(), activityDBController, questionDBController, utils);


        this.game = new Game(new GameUpdateManager(new FakeSimpMessagingTemplate()), questionGenerator);
        this.game.setUUID(uuid);
        this.game.setGameType(GameType.MULTIPLAYER);

    }

    @Test
    public void testConstructor() {

        assertNotNull(game);
        assertEquals(uuid, game.getUUID());
        assertEquals(GameType.MULTIPLAYER, game.getGameType());

    }

    @Test
    public void testAddAndGetPlayer() {

        game.addPlayer(player1);
        game.addPlayer(player2);

        assertEquals(game.getPlayer("P1"), player1);
        assertEquals(game.getPlayer("P2"), player2);
        assertNull(game.getPlayer("P3"));

    }

    @Test
    public void testGetAllPlayers() {

        game.addPlayer(player1);
        game.addPlayer(player2);

        assertEquals(List.of(player1, player2), game.getPlayers());

    }

    @Test
    public void testRemovePlayer() {

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.removePlayer(player2);

        assertEquals(List.of(player1), game.getPlayers());

    }

    @Test
    public void testRemovePlayerByUsername() {

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.removePlayer("P2");

        assertEquals(List.of(player1), game.getPlayers());

    }

    @Test
    public void testContainsPlayer() {

        game.addPlayer(player1);

        assertTrue(game.containsPlayer(player1));
        assertFalse(game.containsPlayer(player2));

    }

    @Test
    public void testContainsPlayerByUsername() {

        game.addPlayer(player1);

        assertTrue(game.containsPlayer("P1"));
        assertFalse(game.containsPlayer("P2"));

    }

    @Test
    public void testEqualsAndHashCodeEqual() {

        assertEquals(game, game);
        assertEquals(game.hashCode(), game.hashCode());

        Game game2 = new Game(new GameUpdateManager(new FakeSimpMessagingTemplate()), questionGenerator);
        game2.setUUID(uuid);
        game2.setGameType(GameType.MULTIPLAYER);
        assertEquals(game, game2);
        assertEquals(game.hashCode(), game2.hashCode());

        game.addPlayer(player1);
        game2.addPlayer(player1);
        assertEquals(game, game2);
        assertEquals(game.hashCode(), game2.hashCode());

    }

    @Test
    public void testEqualsAndHashCodeNotEqual() {

        Game game0 = new Game(new GameUpdateManager(new FakeSimpMessagingTemplate()), questionGenerator);
        game0.setUUID(uuid);
        game0.setGameType(GameType.SINGLEPLAYER);
        assertNotEquals(game, game0);
        assertNotEquals(game.hashCode(), game0.hashCode());

        Game game2 = new Game(new GameUpdateManager(new FakeSimpMessagingTemplate()), questionGenerator);
        game2.setUUID(UUID.randomUUID());
        game2.setGameType(GameType.MULTIPLAYER);
        assertNotEquals(game, game2);
        assertNotEquals(game.hashCode(), game2.hashCode());

        Game game3 = new Game(new GameUpdateManager(new FakeSimpMessagingTemplate()), questionGenerator);
        game3.setUUID(uuid);
        game3.setGameType(GameType.MULTIPLAYER);
        game3.addPlayer(player1);
        assertNotEquals(game, game3);
        assertNotEquals(game.hashCode(), game3.hashCode());

        Game game4 = new Game(new GameUpdateManager(new FakeSimpMessagingTemplate()), questionGenerator);
        game4.setUUID(uuid);
        game4.setGameType(GameType.MULTIPLAYER);
        game.addPlayer(player1);
        game4.addPlayer(player2);
        assertNotEquals(game, game4);
        assertNotEquals(game.hashCode(), game4.hashCode());

    }

    @Test
    public void testToString() {
        String s = game.toString();
        assertTrue(s.contains(Game.class.getSimpleName()));
        assertTrue(s.contains("\n"));
        assertTrue(s.contains("uuid"));
        assertTrue(s.contains("gameType"));
        assertTrue(s.contains("players"));
        assertFalse(s.contains("gameUpdateManager"));
    }

    @Test
    public void saveScoreToLeaderboard(){
        game.saveScoreToLeaderboard(100, "user");
        int points = game.getLeaderboard().get("user").getScore();
        assertEquals(100, points);
    }

    @Test
    public void createLeaderboardList(){
        game.saveScoreToLeaderboard(100, "user");
        List<Score> list = new ArrayList<>();
        list.add(new Score("user", 100));
        assertEquals(list, game.createLeaderboardList());
    }

    @Test
    public void getQuestions(){
        List<Question> list = new ArrayList<>();
        assertEquals(list, game.getQuestions());
    }

    @Test
    public void isDone(){
        assertFalse(game.isDone());
    }

    @Test
    public void setUUID(){
        game.setUUID(UUID.randomUUID());
        assertNotEquals(uuid, game.getUUID());
    }

    @Test
    public void setGameType(){
        GameType gameType = GameType.MULTIPLAYER;
        game.setGameType(gameType);
        assertEquals(GameType.MULTIPLAYER, game.getGameType());
    }

}
