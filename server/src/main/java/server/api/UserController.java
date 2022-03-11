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
     * Creates the player entity and sets the username for it, and attempts to have this player join the
     * current game.
     * @param username the requested username for this player
     * @return 200 OK: GameUpdateFullPlayerList if the player has joined the current game, or 200 OK:
     * GameUpdateNameInUse if the current username is already in use in the current game, and the player
     * can therefore not join the current game with the specified username
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

    /**
     * Removes the player with the specified username from the specified game
     * @param username the username of the player that is leaving
     * @param gameUUIDString the UUID game that the player is in
     * @return 200 OK: regardless of whether the player was actually in the game or whether
     * the game even existed, or 400 Bad Request if the UUID was malformed
     */
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
