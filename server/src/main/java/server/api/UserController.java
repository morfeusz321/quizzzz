package server.api;

import commons.gameupdate.GameUpdate;
import commons.gameupdate.GameUpdateFullPlayerList;
import commons.gameupdate.GameUpdateNameInUse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import commons.Player;
import server.game.GameController;

import java.util.UUID;

/**
 * The API controller for the user. Controls everything mapped to /api/user/...
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private GameController gameController;

    /**
     * Creates the API controller
     */
    public UserController(GameController gameController) {

        this.gameController = gameController;

    }

    /**
     * Maps to /api/user/enter
     * Creates the player entity and sets the username for it
     * @return a text that the user has entered the name successfully
     */
    @PostMapping("/enter")
    public ResponseEntity<GameUpdate> getUserName(@RequestParam("username") String username) {

        Player player = new Player(username);
        boolean playerAdded = gameController.addPlayerToCurrentGame(player);
        if(!playerAdded) {
            return ResponseEntity.ok(new GameUpdateNameInUse());
        }

        return ResponseEntity.ok(new GameUpdateFullPlayerList(gameController.getCurrentGamePlayers(), gameController.getCurrentGameUUID()));

    }

    @PostMapping("/leave")
    public ResponseEntity<String> leaveGame(@RequestParam("username") String username,
                                            @RequestParam("gameUUID") String gameUUIDString) {
        UUID uuid;
        try {
            uuid = UUID.fromString(gameUUIDString);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        gameController.removePlayerFromGame(username, uuid);
        return ResponseEntity.ok().build();

    }

}
