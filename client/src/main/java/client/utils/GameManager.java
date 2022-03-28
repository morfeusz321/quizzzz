package client.utils;

import commons.Question;

import java.util.List;

/**
 * This is the client-side GameManager. It manages the game this client is currently in, e.g. the questions, the
 * current question, etc.
 */
public class GameManager {
    private List<Question> questions;
    private Question currentQuestion;

    /**
     * The (empty) constructor of the GameManager.
     */
    public GameManager(){
    }

    /**
     * Returns the list of questions
     * @return the list of questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Sets the list of questions
     * @param questions the list of questions
     */
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    /**
     * Returns the current question
     * @return the current question
     */
    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * Sets the current question to a question at a specific index in the question list
     * @param idx the index in the question list
     */
    public void setCurrentQuestionByIdx(int idx) {
        this.currentQuestion = questions.get(idx);
    }
}
