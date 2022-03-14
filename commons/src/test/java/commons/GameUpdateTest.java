package commons;

import commons.gameupdate.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameUpdateTest {

    private Player player1;
    private Player player2;
    private List<Player> playerList;

    private UUID gameUUID;

    private GameUpdate gameUpdateFullPlayerList;
    private GameUpdate gameUpdateGameStarting;
    private GameUpdate gameUpdateNameInUse;
    private GameUpdate gameUpdatePlayerJoined;
    private GameUpdate gameUpdatePlayerLeft;

    @BeforeEach
    public void setup() {

        this.player1 = new Player("P1");
        this.player2 = new Player("P2");
        this.playerList = List.of(player1, player2);

        this.gameUUID = UUID.randomUUID();

        this.gameUpdateFullPlayerList = new GameUpdateFullPlayerList(playerList, gameUUID);
        this.gameUpdateGameStarting = new GameUpdateGameStarting();
        this.gameUpdateNameInUse = new GameUpdateNameInUse();
        this.gameUpdatePlayerJoined = new GameUpdatePlayerJoined(player1);
        this.gameUpdatePlayerLeft = new GameUpdatePlayerLeft(player1);

    }

    @Test
    public void testConstructor() {

        assertNotNull(gameUpdateFullPlayerList);
        assertEquals(List.of(player1, player2), ((GameUpdateFullPlayerList) gameUpdateFullPlayerList).getPlayerList());
        assertEquals(gameUUID, ((GameUpdateFullPlayerList) gameUpdateFullPlayerList).getGameUUID());

        assertNotNull(gameUpdateGameStarting);

        assertNotNull(gameUpdateNameInUse);

        assertNotNull(gameUpdatePlayerJoined);
        assertEquals(player1, ((GameUpdatePlayerJoined) gameUpdatePlayerJoined).getPlayer());

        assertNotNull(gameUpdatePlayerLeft);
        assertEquals(player1, ((GameUpdatePlayerLeft) gameUpdatePlayerLeft).getPlayer());

    }

    @Test
    public void testEqualsAndHashCodeEqual() {

        GameUpdate gameUpdateFullPlayerList2 = new GameUpdateFullPlayerList(playerList, gameUUID);
        GameUpdate gameUpdateGameStarting2 = new GameUpdateGameStarting();
        GameUpdate gameUpdateNameInUse2 = new GameUpdateNameInUse();
        GameUpdate gameUpdatePlayerJoined2 = new GameUpdatePlayerJoined(player1);
        GameUpdate gameUpdatePlayerLeft2 = new GameUpdatePlayerLeft(player1);

        assertEquals(gameUpdateFullPlayerList, gameUpdateFullPlayerList2);
        assertEquals(gameUpdateGameStarting, gameUpdateGameStarting2);
        assertEquals(gameUpdateNameInUse, gameUpdateNameInUse2);
        assertEquals(gameUpdatePlayerJoined, gameUpdatePlayerJoined2);
        assertEquals(gameUpdatePlayerLeft, gameUpdatePlayerLeft2);

        assertEquals(gameUpdateFullPlayerList.hashCode(), gameUpdateFullPlayerList2.hashCode());
        assertEquals(gameUpdateGameStarting.hashCode(), gameUpdateGameStarting2.hashCode());
        assertEquals(gameUpdateNameInUse.hashCode(), gameUpdateNameInUse2.hashCode());
        assertEquals(gameUpdatePlayerJoined.hashCode(), gameUpdatePlayerJoined2.hashCode());
        assertEquals(gameUpdatePlayerLeft.hashCode(), gameUpdatePlayerLeft2.hashCode());

    }

    @Test
    public void testEqualsAndHashCodeNotEqual() {

        GameUpdate gameUpdateFullPlayerList2 = new GameUpdateFullPlayerList(List.of(new Player("P3")), gameUUID);
        GameUpdate gameUpdateFullPlayerList3 = new GameUpdateFullPlayerList(playerList, UUID.randomUUID());
        GameUpdate gameUpdatePlayerJoined2 = new GameUpdatePlayerJoined(new Player("P3"));
        GameUpdate gameUpdatePlayerLeft2 = new GameUpdatePlayerLeft(new Player("P3"));

        assertNotEquals(gameUpdateFullPlayerList, gameUpdateFullPlayerList2);
        assertNotEquals(gameUpdateFullPlayerList, gameUpdateFullPlayerList3);
        assertNotEquals(gameUpdatePlayerJoined, gameUpdatePlayerJoined2);
        assertNotEquals(gameUpdatePlayerLeft, gameUpdatePlayerLeft2);

        assertNotEquals(gameUpdateFullPlayerList.hashCode(), gameUpdateFullPlayerList2.hashCode());
        assertNotEquals(gameUpdateFullPlayerList.hashCode(), gameUpdateFullPlayerList3.hashCode());
        assertNotEquals(gameUpdatePlayerJoined.hashCode(), gameUpdatePlayerJoined2.hashCode());
        assertNotEquals(gameUpdatePlayerLeft.hashCode(), gameUpdatePlayerLeft2.hashCode());

    }

    @Test
    public void testToString() {

        String gameUpdateFullPlayerListToString = gameUpdateFullPlayerList.toString();
        assertTrue(gameUpdateFullPlayerListToString.contains(GameUpdateFullPlayerList.class.getSimpleName()));
        assertTrue(gameUpdateFullPlayerListToString.contains("\n"));
        assertTrue(gameUpdateFullPlayerListToString.contains("playerList"));
        assertTrue(gameUpdateFullPlayerListToString.contains("gameUUID"));

        String gameUpdateGameStartingToString = gameUpdateGameStarting.toString();
        assertTrue(gameUpdateGameStartingToString.contains(GameUpdateGameStarting.class.getSimpleName()));
        assertTrue(gameUpdateGameStartingToString.contains("\n"));

        String gameUpdateNameInUseToString = gameUpdateNameInUse.toString();
        assertTrue(gameUpdateNameInUseToString.contains(GameUpdateNameInUse.class.getSimpleName()));
        assertTrue(gameUpdateNameInUseToString.contains("\n"));

        String gameUpdatePlayerJoinedToString = gameUpdatePlayerJoined.toString();
        assertTrue(gameUpdatePlayerJoinedToString.contains(GameUpdatePlayerJoined.class.getSimpleName()));
        assertTrue(gameUpdatePlayerJoinedToString.contains("\n"));
        assertTrue(gameUpdatePlayerJoinedToString.contains("player"));

        String gameUpdatePlayerLeftToString = gameUpdatePlayerLeft.toString();
        assertTrue(gameUpdatePlayerLeftToString.contains(GameUpdatePlayerLeft.class.getSimpleName()));
        assertTrue(gameUpdatePlayerLeftToString.contains("\n"));
        assertTrue(gameUpdatePlayerLeftToString.contains("player"));

    }

}
