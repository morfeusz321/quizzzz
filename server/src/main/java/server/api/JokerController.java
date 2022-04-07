package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.game.Game;
import server.game.GameController;

import java.util.UUID;

@RestController
@RequestMapping("/api/jokers")
public class JokerController {

    private final GameController gameController;

    /**
     * Creates the API controller
     */
    public JokerController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Maps to /api/jokers/time. Used for triggering the time joker for all players except the player who used the joker
     *
     * @param username     the player who used the joker
     * @param gameIDString The UUID of the current game
     * @return 400 Bad request: the UUID is wrong or the game does not exist, 200 OK: the username is returned to the client
     */
    @PostMapping("/time")
    public ResponseEntity<String> useTimeJoker(@RequestParam("username") String username, @RequestParam("gameUUID") String gameIDString) {
        UUID uuid;
        try {
            uuid = UUID.fromString(gameIDString);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameController.getGame(uuid);
        if(game == null) return ResponseEntity.badRequest().build();

        game.useTimeJoker(username);

        return ResponseEntity.ok(username);
    }

    /**
     * Maps to /api/jokers/question. Used for triggering the question joker for a player
     *
     * @param username     the player who used the joker
     * @param gameIDString The UUID of the current game
     * @return 400 Bad request: the UUID is wrong or the game does not exist, 200 OK: the username is returned to the client
     */
    @PostMapping("/question")
    public ResponseEntity<String> useAnswerJoker(@RequestParam("username") String username, @RequestParam("gameUUID") String gameIDString) {
        UUID uuid;
        try {
            uuid = UUID.fromString(gameIDString);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameController.getGame(uuid);
        if(game == null) return ResponseEntity.badRequest().build();

        game.useQuestionJoker(username);

        return ResponseEntity.ok(username);
    }

    /**
     * Maps to /api/jokers/score. Used for triggering the double points joker for a player
     *
     * @param username     the player who used the joker
     * @param gameIDString The UUID of the current game
     * @return 400 Bad request: the UUID is wrong or the game does not exist, 200 OK: the username is returned to the client
     */
    @PostMapping("/score")
    public ResponseEntity<String> useScoreJoker(@RequestParam("username") String username, @RequestParam("gameUUID") String gameIDString) {
        UUID uuid;
        try {
            uuid = UUID.fromString(gameIDString);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameController.getGame(uuid);
        if(game == null) return ResponseEntity.badRequest().build();

        game.useScoreJoker(username);

        return ResponseEntity.ok(username);
    }

}
