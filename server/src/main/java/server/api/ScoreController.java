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

 /*   @PostMapping(path={"{username}"})
    public ResponseEntity<Score> addNewScore(@RequestParam("username") String username){
        if(Objects.equals(username, "")){
            return ResponseEntity.noContent().build();
        }
        Score saved;
        try{
            saved = addScore(username,  0);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(saved);
    }

    @PostMapping(path={"{username}/{points}"})
    public ResponseEntity<Score> editScore(@RequestParam("username") String username,
                                           @RequestParam("points") String points){
        if(username.equals("")){
            return ResponseEntity.noContent().build();
        }
        Score saved;
        try{
            saved = addScore(username, getByName(username).getScore() + Integer.parseInt(points));
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(saved);
    }*/

    /**
     * Maps to /api/scores/ and /api/scores
     * @return all scores saved in the database
     */
    @GetMapping(path = { "", "/" })
    public List<Score> getAll() {
        return scoreDBController.findAll();
    }

    /**
     * Finds all scores in the database, sorted by leaderboard rank ascending. Maps to /api/scores/sorted
     * @return all scores saved in the database, sorted by leaderboard rank ascending
     */
    @GetMapping("/sorted")
    public List<Score> getAllSorted() {
        return scoreDBController.findAllSorted();
    }


   /* @GetMapping(path = {"/{username}" })
    public Score getByName(@PathVariable("username") String username) {
        return scoreDBController.getScoreByName(username);
    }*/
}
