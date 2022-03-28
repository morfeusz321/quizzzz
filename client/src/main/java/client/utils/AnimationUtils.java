package client.utils;

import client.scenes.MainCtrl;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class AnimationUtils {
    private static final long ANIMATION_MILLISECONDS = 400L;

    /**
     * fading transition and depending on nextScene it goes to that scene
     * @param anchorPane anchorPane of the Controller
     * @param mainCtrl main controller
     * @param nextScene next scene to be shown
     */
    public void fadeOut(AnchorPane anchorPane, MainCtrl mainCtrl, String nextScene){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(ANIMATION_MILLISECONDS));
        fadeTransition.setNode(anchorPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        switch(nextScene){
            case "main":
                fadeTransition.setOnFinished((ActionEvent event) -> mainCtrl.showMainScreen());
                break;
            case "user":
                fadeTransition.setOnFinished((ActionEvent event) -> mainCtrl.showUsernameInputScreen());
                break;
            case "help":
                fadeTransition.setOnFinished((ActionEvent event) -> mainCtrl.showHelpScreen());
                break;
            case "admin":
                fadeTransition.setOnFinished((ActionEvent event) -> mainCtrl.showAdmin());
                break;
            case "adminEdit":
                fadeTransition.setOnFinished((ActionEvent event) -> mainCtrl.showAdminServerConfirmed());
                break;
            case "wait":
                fadeTransition.setOnFinished((ActionEvent event) -> mainCtrl.showWaitingRoom());
                break;
            case "leaderboard":
                fadeTransition.setOnFinished((ActionEvent event) -> mainCtrl.showLeaderboardServerConfirmed());
                break;
            case "gameAgain":
                fadeTransition.setOnFinished((ActionEvent event) -> mainCtrl.showWaitRoomFromTheEndScreen());
                break;
            default:
                fadeTransition.setOnFinished((ActionEvent event) -> mainCtrl.showMainScreen());
                break;
        }
        fadeTransition.play();
    }

    /**
     * fading transition
     * @param anchorPane
     */
    public void fadeIn(AnchorPane anchorPane) {
        anchorPane.setOpacity(0);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(ANIMATION_MILLISECONDS));
        fadeTransition.setNode(anchorPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }
}
