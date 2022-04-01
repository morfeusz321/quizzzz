package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class AnswerResponseEntity {

    public boolean correct;
    public long proximity;
    private long answer;
    private int points;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private AnswerResponseEntity() {



    }

    /**
     * gets the answer number
     * @return the number of the answer
     */
    public long getAnswer() {
        return answer;
    }

    /**
     * gets the points received
     * @return amount of points
     */
    public int getPoints(){
        return points;
    }

    /**
     * Creates a new answer response entity (used for general and comparison questions)
     * @param correct whether the answer was correct or not
     */
    public AnswerResponseEntity(boolean correct, long answer) {

        this.correct = correct;
        this.proximity = 0;
        this.answer = answer;

    }

    /**
     * Creates a new answer response entity (used for estimation questions)
     * @param correct whether the answer should be displayed as correct or not
     * @param proximity the difference between the answer and the correct answer
     */
    public AnswerResponseEntity(boolean correct, long proximity, long answer) {

        this.correct = correct;
        this.proximity = proximity;
        this.answer = answer;
        this.points = 0;

    }

    /**
     * Creates a new answer response entity (also with points given for the answer)
     * for estimation questions
     * @param correct whether the answer should be displayed as correct or not
     * @param proximity the difference between the answer and the correct answer
     * @param answer which button was clicked
     * @param points points given for the question
     */
    public AnswerResponseEntity(boolean correct,long proximity, long answer, int points) {

        this.correct = correct;
        this.answer = answer;
        this.points = points;
        this.proximity = proximity;

    }

    /**
     * Convenience factory method to automatically generate an AnswerResponseEntity for a given question
     * and answer
     * @param q the question that is being answered
     * @param answer the answer given
     * @return an applicable AnswerResponseEntity for the given question and answer
     */
    public static AnswerResponseEntity generateAnswerResponseEntity(Question q, long answer, int time) {
        long prox = q.answer - answer;
        boolean cor = answer == q.answer;

        if(q instanceof EstimationQuestion) {
            return new AnswerResponseEntity(cor, prox, q.answer, dynamicPointsEstimation(prox, answer, time));
        } else {
            return new AnswerResponseEntity(cor, prox, q.answer, dynamicPointsMultipleChoice(cor, time) );
        }

    }

    /**
     * depending on how close the user is to the answer the amount of points is given
     * @param proximity how close the user is to the answer
     * @param answer if the answer is correct or no
     * @param time the time passed till answering the question
     * @return number of points given
     */
    public static int dynamicPointsEstimation(long proximity, long answer, int time){
        if (proximity == 0){
            return 100;
        }
        double percentagePassed = Math.abs(((double) proximity) /answer);
        if(percentagePassed<0.21) {
            percentagePassed = Math.abs(1-((double) proximity) /answer);
            return (((int)(( ((1/(0.4*Math.sqrt(2*Math.PI)))* Math.exp(-0.5*Math.pow(((percentagePassed-1)/0.14), 2))))*100 +1)) + dynamicPointsMultipleChoice(true, time))/2;
        }
        return 0;
    }

    /**
     * depending on when the user clicks on the answer the amount of points is given
     * @param correct answer is correct or not
     * @param time hwo fast the user clicked on the answer
     * @return number of points given
     */
    public static int dynamicPointsMultipleChoice(boolean correct, int time){
        if(correct){
            double percentagePassed =  ((double) time) /15000L;
            if(percentagePassed<0.21){
                return 100;
            }
            else{
                return (int) ((Math.exp(-3*percentagePassed) + 0.46) * 100);
            }
        }
        else{
            return 0;
        }
    }

    /**
     * Checks if 2 answer response entity objects are equal
     * @param obj the object that will be compared
     * @return true or false, whether the objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generate a hash code for this object
     * @return hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Creates a formatted string for this object
     * @return a formatted string in multi line style
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
