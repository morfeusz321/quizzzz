package commons.gameupdate;

import commons.Score;

import java.util.List;

/**
 * This GameUpdate is sent to a client to inform it of the fact that it should be displaying the leaderboard
 * that divides the game into two phases
 */
public class GameUpdateDisplayLeaderboard extends GameUpdate {

    private List<Score> leaderboard;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdateDisplayLeaderboard() {

    }

    /**
     * Creates a new GameUpdate
     * @param leaderboard the leaderboard to be displayed by the client
     */
    public GameUpdateDisplayLeaderboard(List<Score> leaderboard) {

        this.leaderboard = leaderboard;

    }

    /**
     * Returns the leaderboard to be displayed by the client
     * @return the leaderboard to be displayed by the client
     */
    public List<Score> getLeaderboard() {

        return this.leaderboard;

    }

}
