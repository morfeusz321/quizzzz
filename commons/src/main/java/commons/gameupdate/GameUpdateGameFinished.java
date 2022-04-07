package commons.gameupdate;

import commons.Score;

import java.util.List;

/**
 * This GameUpdate is sent to a client to inform it of the fact that the current game has finished
 */
public class GameUpdateGameFinished extends GameUpdate {

    private List<Score> leaderboard;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdateGameFinished() {

    }

    /**
     * Creates a new GameUpdate
     *
     * @param leaderboard the leaderboard to be displayed by the client
     */
    public GameUpdateGameFinished(List<Score> leaderboard) {

        this.leaderboard = leaderboard;

    }

    /**
     * Returns the leaderboard to be displayed by the client
     *
     * @return the leaderboard to be displayed by the client
     */
    public List<Score> getLeaderboard() {

        return this.leaderboard;

    }

}
