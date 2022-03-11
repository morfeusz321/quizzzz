package commons.gameupdate;

import commons.Player;

import java.util.List;
import java.util.UUID;

/**
 * This GameUpdate is sent to a client after joining the game, and gives it a list of all players currently in the
 * game, as well as the UUID of the game it joined.
 */
public class GameUpdateFullPlayerList extends GameUpdate {

    private List<Player> playerList;
    private UUID gameUUID;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdateFullPlayerList() {

    }

    /**
     * Creates a new GameUpdate
     * @param playerList the list of players in the game
     * @param gameUUID the UUID of the game
     */
    public GameUpdateFullPlayerList(List<Player> playerList, UUID gameUUID) {

        this.playerList = playerList;
        this.gameUUID = gameUUID;

    }

    /**
     * Returns the list of all players in the game
     * @return the list of all players in the game
     */
    public List<Player> getPlayerList() {

        return this.playerList;

    }

    /**
     * Returns the UUID of the game
     * @return the UUID of the game
     */
    public UUID getGameUUID() {

        return this.gameUUID;

    }

}
