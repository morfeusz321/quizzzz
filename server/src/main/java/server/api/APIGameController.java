package server.api;

import commons.*;
import commons.gameupdate.GameUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.game.Game;
import server.game.GameController;

import java.util.List;
import java.util.UUID;

/**
 * The API controller for the games. Controls everything mapped to /api/game/...
 */
@RestController
@RequestMapping("/api/game")
public class APIGameController {

    private GameController gameController;

    /**
     * Creates the API controller
     */
    public APIGameController(GameController gameController) {

        this.gameController = gameController;

    }

    /**
     * Maps to /game/questions?gameID=id
     * Returns the list of questions of a Game if there is one that has the corresponding UUID
     *
     * @return 200 OK: List of all questions,
     * 400 Bad Request if the UUID is malformed or there is no running game with it
     * 500 Internal Server Error if no question(s) could be generated and the game does not have 20 questions
     */
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getAllQuestions(@RequestParam("gameID") String gameIDString) {

        UUID uuid;
        try {
            uuid = UUID.fromString(gameIDString);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameController.getGame(uuid);

        if(game == null || game.equals(gameController.getCurrentGame())) {
            return ResponseEntity.badRequest().build();
        }
        List<Question> questions = game.getQuestions();
        if(questions == null || questions.size() != 20){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(questions);

    }

    /**
     * Provides game loop updates in the form of a deffered result to allow for
     * long polling to this endpoint. Maps to api/game/
     * @param gameIDString the UUID of the game whose updates the client wishes to
     *                     subscribe to
     * @param username the name of the player
     * @return 200 OK: game loop update, or bad request if the game UUID does not exist.
     * Can also return an internal server error in case of timeout.
     */
    @GetMapping("/")
    public DeferredResult<ResponseEntity<GameUpdate>> gameLongPollLoop(@RequestParam("gameID") String gameIDString, @RequestParam("username") String username) {

        DeferredResult<ResponseEntity<GameUpdate>> result = new DeferredResult<>(40000L, ResponseEntity.internalServerError().build());

        UUID uuid;
        try {
            uuid = UUID.fromString(gameIDString);
        } catch (IllegalArgumentException e) {
            result.setResult(ResponseEntity.badRequest().build());
            return result;
        }

        Game game = gameController.getGame(uuid);
        if(game == null || game.equals(gameController.getCurrentGame())) {
            result.setResult(ResponseEntity.badRequest().build());
            return result;
        }

        if(!game.containsPlayer(username)) {
            result.setResult(ResponseEntity.badRequest().build());
            return result;
        }

        game.runDeferredResult(username, result);
        return result;

    }

    /**
     * maps to /api/game/answer
     * saves the answer to the Concurrent map in the game class, answers the http request
     * @param gameIDString the UUID string of the game
     * @param playerName the username of the player
     * @param answerString the chosen answer by the player
     * @return response entity containing information about the answer: whether the answer was correct,
     *  the answer, and proximity to the correct answer for the estimation question
     */
    @PostMapping("/answer")
    public ResponseEntity<String> answer(@RequestParam("gameID") String gameIDString,
                                         @RequestParam("playerName") String playerName,
                                         @RequestParam("answer") String answerString) {
        long answer;
        try {
            answer = Long.parseLong(answerString);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        UUID gameID;
        try {
            gameID = UUID.fromString(gameIDString);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameController.getGame(gameID);

        if(game == null || game.equals(gameController.getCurrentGame())) {
            return ResponseEntity.badRequest().build();
        }

        if(!game.containsPlayer(playerName)) {
            return ResponseEntity.badRequest().build();
        }

        game.saveAnswer(playerName, answer);

        return ResponseEntity.ok().build();

    }

    /**
     * retrieve all players from the game
     * @param gameIDString game UUID of the game
     * @return List of all current players
     */
    @GetMapping("/players")
    public ResponseEntity<List<Player>> getAllPlayers(@RequestParam("gameID") String gameIDString) {

        UUID uuid;
        try {
            uuid = UUID.fromString(gameIDString);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameController.getGame(uuid);

        if(game == null || game.equals(gameController.getCurrentGame())) {
            return ResponseEntity.badRequest().build();
        }

        List<Player> players = game.getPlayers();
        if( players== null){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(players);

    }


}
