package commons.gameupdate;

import commons.Player;

/**
 * This GameUpdate is sent to a client to inform it of the fact that a player has left
 * the current game.
 */
public class GameUpdatePlayerLeft extends GameUpdate {

    private Player player;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdatePlayerLeft() {

    }

    /**
     * Creates a new GameUpdate
     *
     * @param player the player that has left the game
     */
    public GameUpdatePlayerLeft(Player player) {

        this.player = player;

    }

    /**
     * Returns the player that has left the game
     *
     * @return the player that has left the game
     */
    public Player getPlayer() {

        return this.player;

    }

}
