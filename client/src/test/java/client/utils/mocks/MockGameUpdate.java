package client.utils.mocks;

import commons.gameupdate.GameUpdate;

/**
 * The MockGameUpdate holds is intended for testing and just holds a single String for testing purposes
 */
public class MockGameUpdate extends GameUpdate {

    private String info;

    /**
     * Creates a new MockGameUpdate with the given information
     *
     * @param info the information this MockGameUpdate should hold
     */
    public MockGameUpdate(String info) {

        this.info = info;

    }

    /**
     * Returns this GameUpdate's information
     *
     * @return this GameUpdate's information
     */
    public String getInfo() {

        return this.info;

    }

    /**
     * Sets this GameUpdate's information
     *
     * @param info the information to set
     */
    public void setInfo(String info) {

        this.info = info;

    }

}
