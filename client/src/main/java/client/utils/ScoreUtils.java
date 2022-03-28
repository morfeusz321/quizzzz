package client.utils;

import commons.AnswerResponseEntity;
import commons.Player;

public class ScoreUtils {
    Player player;
    int lastClickedTime;

    /**
     * constructor
     */
    public ScoreUtils(){
        player = null;
    }

    /**
     * given answer correct/incorrect it gives out points to the player
     * @param answer answer from GameUpdate
     */
    public void setScore(AnswerResponseEntity answer){
        int pointsGiven = dynamicPointsMultipleChoice(answer.correct);
        this.player.increasePoints(pointsGiven);
    }

    /**
     * depending on when the user clicks on the answer the amount of points is given
     * @param correct answer is correct or not
     * @return number of points given
     */
    private int dynamicPointsMultipleChoice(boolean correct){
        System.out.println(this.lastClickedTime);
        if(correct){
            return 200;
        }
        else{
            return 0;
        }
    }

    /**
     * sets the player
     * @param player another player object
     */
    public void setPlayer(Player player){
        this.player = player;
    }

    /**
     * retrieves the points of the player
     * @return number of points the player has
     */
    public int getPoints(){
        return player.getPoints();
    }
}
