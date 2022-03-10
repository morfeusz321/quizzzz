package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;


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
}
