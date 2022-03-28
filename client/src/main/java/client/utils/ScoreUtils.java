package client.utils;

import commons.AnswerResponseEntity;
import commons.Player;

public class ScoreUtils {
    Player player;

    public ScoreUtils(){
        player = null;
    }

    public void setScore(AnswerResponseEntity answer){
        int pointsGiven = dynamicPointsMultipleChoice(answer.correct);
        this.player.increasePoints(pointsGiven);
        System.out.println(player.getUsername()+ ": " + pointsGiven);
    }

    private int dynamicPointsMultipleChoice(boolean correct){
        if(correct){
            return 200;
        }
        else{
            return 0;
        }
    }

    public void setPlayer(Player player){
        this.player = player;
    }
}
