package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;


/**
 * The player class
 */
public class Player {

    private String username;
    private int points;

    /**
     * Constructor of the player class
     */
    public Player() {
        points = 0;
    }

    /**
     * gets the username of the player
     * @return username of the player
     */
    public String getUsername() {
        return username;
    }

    /**
     * gets the points of the player
     * @return the points of the player
     */
    public int getPoints() {
        return points;
    }

    /**
     * a setter for the player username
     * @param username - the username of the player
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * increases the points of the player by x
     * @param x amount of points to be increased
     */
    public void increasePoints(int x){
        points+=x;
    }


    /**
     * decreases the points of the player by x
     * @param x amount of points to be decreased
     */
    public void decreasePoints(int x){
        points-=x;
    }

    /**
     * an equals method for the player class
     * @param o - an object to be compared
     * @return boolean whether the object is equal to the player
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }


    /**
     * hashes a code for this object
     * @return hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Creates a string for this subject
     * @return a formatted string in multi line style
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
