package commons.gameupdate;

import commons.AnswerResponseEntity;

/**
 * This GameUpdate is sent to a client to inform it of the fact that the current game is entering the transition
 * period between questions
 */
public class GameUpdateTransitionPeriodEntered extends GameUpdate {

    private AnswerResponseEntity answerResponseEntity;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdateTransitionPeriodEntered() {

    }

    /**
     * Creates a new GameUpdate
     *
     * @param answerResponseEntity the answer response entity containing the necessary information for the
     *                             client to display the transition screen
     */
    public GameUpdateTransitionPeriodEntered(AnswerResponseEntity answerResponseEntity) {

        this.answerResponseEntity = answerResponseEntity;

    }

    /**
     * Returns the answer response entity containing the necessary information for the client to display
     * the transition screen
     *
     * @return the answer response entity containing the necessary information for the client to display
     * the transition screen
     */
    public AnswerResponseEntity getAnswerResponseEntity() {

        return this.answerResponseEntity;

    }

}
