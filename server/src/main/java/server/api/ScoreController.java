package server.api;

import commons.*;
import org.springframework.http.ResponseEntity;
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
     * Maps to /api/scores/add?user=name&score=score
     * @param username the username for which to save the score (post variable user)
     * @param scoreString the score to save, as a string (post variable score)
     * @return 200 OK if a score was saved, 400 Bad Request if the POST request is malformed
     */
    @PostMapping("/add")
    public ResponseEntity<Score> addScore(@RequestParam("user") String username,
                                          @RequestParam("score") String scoreString) {
        int score;
        try {
            score = Integer.parseInt(scoreString);
        } catch(NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        Score saved = scoreDBController.add(new Score(username, score));
        return ResponseEntity.ok(saved);
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
