package server.api;

import commons.GameType;
import commons.gameupdate.GameUpdate;
import commons.gameupdate.GameUpdateFullPlayerList;
import commons.gameupdate.GameUpdateNameInUse;
import commons.gameupdate.GameUpdateNameTooLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import commons.Player;
import server.database.ScoreDBController;
import server.game.GameController;

import java.util.Optional;
import java.util.UUID;

/**
 * The API controller for the user. Controls everything mapped to /api/user/...
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    static final int MAXIMUM_USERNAME_SIZE = 15;

    private GameController gameController;
    private ScoreDBController scoreDBController;

    /**
     * Creates the API controller
     */
    public UserController(GameController gameController, ScoreDBController scoreDBController) {

        this.gameController = gameController;
        this.scoreDBController = scoreDBController;

    }

    /**
     * Maps to /api/user/enter
     * Creates the player entity and sets the username for it, and attempts to have this player join the
     * current game for multiplayer games, or creates a new game for it for singleplayer games.
     * If the username is already in use, for multiplayer games, the player will not be able to join. For
     * singleplayer games, the player must include the confirmNameInUse=true parameter in the URL in order to
     * start the game.
     * @param username the requested username for this player
     * @param gametype the game type that the player wishes to join (either GameType.SINGLEPLAYER or GameType.MULTIPLAYER)
     * @param confirmNameInUse confirms that the player knows that the name has already been used on the leaderboard
     *                         before for singleplayer games, and still wishes to use its requested username
     * @return For multiplayer games: 200 OK: GameUpdateFullPlayerList if the player has joined the current game, or 200 OK:
     * GameUpdateNameInUse if the current username is already in use in the current game, and the player
     * can therefore not join the current game with the specified username. For singleplayer games: 200 OK:
     * GameUpdateFullPlayerList if the player has been registered, and a singleplayer game is starting, or
     * 200 OK: GameUpdateNameInUse if the name has already been registered to the leaderboard and the confirmNameInUse
     * parameter is not present or is not set to true. Can also return 400 Bad Request if the gametype parameter is invalid.
     */
    @PostMapping("/enter")
    public ResponseEntity<GameUpdate> joinGame(@RequestParam("username") String username,
                                                  @RequestParam("gametype") String gametype,
                                                  @RequestParam("confirmNameInUse") Optional<String> confirmNameInUse) {

        if(username.length() > MAXIMUM_USERNAME_SIZE) {

            return ResponseEntity.ok(new GameUpdateNameTooLong());

        }

        if(gametype.equals(GameType.SINGLEPLAYER.name())) {

            if(scoreDBController.getScoreByName(username) != null) {

                if(confirmNameInUse.isPresent()) {

                    if(confirmNameInUse.get().equals("true")) {

                        Player player = new Player(username);
                        return ResponseEntity.ok(gameController.createSinglePlayerGame(player));

                    } else {

                        return ResponseEntity.ok(new GameUpdateNameInUse());

                    }

                } else {

                    return ResponseEntity.ok(new GameUpdateNameInUse());

                }

            } else {

                Player player = new Player(username);
                return ResponseEntity.ok(gameController.createSinglePlayerGame(player));

            }

        } else if(gametype.equals(GameType.MULTIPLAYER.name())) {

            Player player = new Player(username);
            boolean playerAdded = gameController.addPlayerToCurrentGame(player);
            if(!playerAdded) {
                return ResponseEntity.ok(new GameUpdateNameInUse());
            }

            return ResponseEntity.ok(new GameUpdateFullPlayerList(gameController.getCurrentGamePlayers(),
                                                                    gameController.getCurrentGameUUID()));

        } else {

            return ResponseEntity.badRequest().build();

        }

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
