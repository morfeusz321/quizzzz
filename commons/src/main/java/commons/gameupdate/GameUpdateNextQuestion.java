package commons.gameupdate;

/**
 * This GameUpdate is sent to a client to inform it of the fact that the current game is moving on
 * to the next question
 */
public class GameUpdateNextQuestion extends GameUpdate {

    private int questionIdx;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdateNextQuestion() {

    }

    /**
     * Creates a new GameUpdate
     * @param questionIdx the index of the question that the game is moving to
     */
    public GameUpdateNextQuestion(int questionIdx) {

        this.questionIdx = questionIdx;

    }

    /**
     * Returns the index of the new question
     * @return the index of the new question
     */
    public int getQuestionIdx() {

        return this.questionIdx;

    }

}
