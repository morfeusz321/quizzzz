package commons.gameupdate;

import commons.Player;

import java.util.List;

public class GameUpdateTimerJoker extends GameUpdate{

    private List<Player> playerList;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdateTimerJoker() {

    }

    /**
     * Creates a new Game Update
     * @param playerList list of current players in the game (excluding one)
     */
    public GameUpdateTimerJoker(List<Player> playerList) {
        this.playerList = playerList;
    }

    /**
     * Method for returning the player list
     * @return the player list
     */
    public List<Player> getPlayerList() {
        return playerList;
    }

}
