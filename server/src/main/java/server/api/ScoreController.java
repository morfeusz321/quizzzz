package server.api;

import commons.*;
import org.springframework.web.bind.annotation.*;
import server.database.ScoreDBController;

import java.util.List;

/**
 * The API controller for the scores. Controls everything mapped to /api/scores/...
 */
@RestController
@RequestMapping("/api/scores")
public class ScoreController {
    private final ScoreDBController scoreDBController;

    /**
     * Creates the API controller
     * @param scoreDBController the interface with the score database to be used by this controller
     */
    public ScoreController(ScoreDBController scoreDBController) {
        this.scoreDBController = scoreDBController;
    }

    /**
     * Adds a score to the database (no API mapping)
     * @param username the username for which to save the score
     * @param score the score to save
     * @return the score that was saved
     */
    public Score addScore(String username, int score) {
        return scoreDBController.add(new Score(username, score));
    }

    /**
     * Maps to /api/scores/ and /api/scores
     * @return all scores saved in the database
     */
    @GetMapping(path = { "", "/" })
    public List<Score> getAll() {
        return scoreDBController.findAll();
    }
}
