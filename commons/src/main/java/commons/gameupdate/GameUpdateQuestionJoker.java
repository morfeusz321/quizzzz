package commons.gameupdate;

import commons.Player;

import java.util.List;

public class GameUpdateQuestionJoker extends GameUpdate{
    private int buttonNumber;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdateQuestionJoker() {

    }

    /**
     * Creates a new Game Update
     * @param buttonNumber the id of the button that will be disabled
     */
    public GameUpdateQuestionJoker(int buttonNumber) {
        this.buttonNumber = buttonNumber;
    }

    /**
     * Getter for button number
     * @return button number
     */
    public int getButtonNumber() {
        return buttonNumber;
    }
}
