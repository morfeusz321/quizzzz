package client.utils;

import commons.AnswerResponseEntity;
import commons.Player;

public class ScoreUtils {

    Player player;

    /**
     * constructor
     */
    public ScoreUtils() {
        player = null;
    }

    /**
     * sets score from the answer from the server
     *
     * @param answer answerResponseEntity from the Server
     */
    public void setScore(AnswerResponseEntity answer) {
        if(answer == null) {
            return;
        }
        int pointsGiven = answer.getPoints();
        this.player.increasePoints(pointsGiven);
    }

    /**
     * sets the player
     *
     * @param player another player object
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * retrieves the points of the player
     *
     * @return number of points the player has
     */
    public int getPoints() {
        return player.getPoints();
    }

}
