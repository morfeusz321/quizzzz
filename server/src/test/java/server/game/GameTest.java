package server.game;

import commons.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Player player1;
    private Player player2;

    private UUID uuid;
    private Game game;

    @BeforeEach
    public void setup() {

        this.player1 = new Player("P1");
        this.player2 = new Player("P2");

        this.uuid = UUID.randomUUID();

        this.game = new Game(uuid);

    }

    @Test
    public void testConstructor() {

        assertNotNull(game);
        assertEquals(uuid, game.getUUID());

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

        Game game2 = new Game(uuid);
        assertEquals(game, game2);
        assertEquals(game.hashCode(), game2.hashCode());

        game.addPlayer(player1);
        game2.addPlayer(player1);
        assertEquals(game, game2);
        assertEquals(game.hashCode(), game2.hashCode());

    }

    @Test
    public void testEqualsAndHashCodeNotEqual() {

        Game game2 = new Game(UUID.randomUUID());
        assertNotEquals(game, game2);
        assertNotEquals(game.hashCode(), game2.hashCode());

        Game game3 = new Game(uuid);
        game3.addPlayer(player1);
        assertNotEquals(game, game3);
        assertNotEquals(game.hashCode(), game3.hashCode());

        Game game4 = new Game(uuid);
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
        assertTrue(s.contains("players"));
    }

}
