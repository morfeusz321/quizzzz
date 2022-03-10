package server.database;

import commons.Quote;
import commons.Score;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoreDBController {
    private final ScoreDB scoreDB;

    /**
     * Creates a controller for the score database (leaderboard)
     * @param scoreDB the score database to be used by this controller
     */
    public ScoreDBController(ScoreDB scoreDB) {
        this.scoreDB = scoreDB;
    }

    /**
     * Deletes all entries in the score database
     */
    public void clear() {
        scoreDB.deleteAll();
    }

    /**
     * Retrieves a Score (username + score) from the database from its unique username, if there is no score yet,
     * null is returned.
     * @param username the username for which to retrieve the score
     * @return if found, the corresponding Score object, otherwise null
     */
    public Score getScoreByName(String username) {
        return scoreDB.findById(username).orElse(null);
    }

    /**
     * Adds a score to the database. If the username was already in the database as a key,
     * overwrite the saved value, if the new one is higher.
     * @param score a Score object (username and score)
     * @return the Score object that was saved (depending on whether a score was overwritten)
     */
    public Score add(Score score) {
        Score saved = getScoreByName(score.username);
        if(saved == null || saved.score < score.score){
            scoreDB.save(score);
            return score;
        }
        return saved;
    }

    public List<Score> findAll() {
        return scoreDB.findAll();
    }
}
