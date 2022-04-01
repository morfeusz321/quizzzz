package commons.gameupdate;

import java.util.concurrent.ConcurrentHashMap;

public class GameUpdateTimerJoker extends GameUpdate{

    private ConcurrentHashMap<String, Long> time;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GameUpdateTimerJoker() {

    }

    /**
     * Creates a new Game Update
     * @param time hash map containing names and times
     */
    public GameUpdateTimerJoker(ConcurrentHashMap<String, Long> time) {
        this.time = time;
    }

    /**
     * Getter for time hash map
     * @return a concurrent hash map with names and times
     */
    public ConcurrentHashMap<String, Long> getTime() {
        return time;
    }
}
