package server.api;

import commons.Question;
import commons.gameupdate.GameUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

        if(game.getQuestions() == null || game.getQuestions().size() != 20){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(game.getQuestions());

    }

    /**
     * Provides game loop updates in the form of a deffered result to allow for
     * long polling to this endpoint. Maps to api/game/
     * @param gameIDString the UUID of the game whose updates the client wishes to
     *                     subscribe to
     * @return 200 OK: game loop update, or bad request if the game UUID does not exist.
     * Can also return an internal server error in case of timeout.
     */
    @GetMapping("/")
    public DeferredResult<ResponseEntity<GameUpdate>> gameLongPollLoop(@RequestParam("gameID") String gameIDString) {

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

        game.runDeferredResult(result);
        return result;

    }

}
