package commons.gameupdate;

import commons.Player;

/**
 * This GameUpdate is sent to a client to inform it of the fact that a new player has joined
 * the current game.
 */
public class GameUpdatePlayerJoined extends GameUpdate {

    private Player player;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdatePlayerJoined() {

    }

    /**
     * Creates a new GameUpdate
     * @param player the player that has joined the game
     */
    public GameUpdatePlayerJoined(Player player) {

        this.player = player;

    }

    /**
     * Returns the player that has joined the game
     * @return the player that has joined the game
     */
    public Player getPlayer() {

        return this.player;

    }

}
