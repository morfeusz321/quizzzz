package server.game;

import commons.Player;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameController {

    private ConcurrentHashMap<UUID, Game> games;
    private Game currentGame;
    private GameUpdateManager gameUpdateManager;

    /**
     * Instantiates a game controller
     * @param gameUpdateManager the update manager for WebSocket messages
     */
    public GameController(GameUpdateManager gameUpdateManager) {

        this.games = new ConcurrentHashMap<>();
        this.currentGame = new Game(UUID.randomUUID());
        this.gameUpdateManager = gameUpdateManager;

    }

    /**
     * Returns the current game (the game that is in the waiting room state)
     * @return the current game
     */
    public Game getCurrentGame() {

        return this.currentGame;

    }

    /**
     * Returns the UUID of the current game (the game that is in the waiting room state)
     * @return the UUID of the current game
     */
    public UUID getCurrentGameUUID() {

        return this.currentGame.getUUID();

    }

    /**
     * Gets a game (either started or in waiting room state) by its UUID
     * @param uuid the UUID of the game to retrieve
     * @return the Game with the specified UUID if it exists, or null if it doesn't
     */
    public Game getGame(UUID uuid) {

        if(this.games.containsKey(uuid)) {
            return this.games.get(uuid);
        } else if(this.currentGame.getUUID().equals(uuid)) {
            return this.currentGame;
        } else {
            return null;
        }

    }

    /**
     * Returns all the players in the current game (the game that is in the waiting room state)
     * @return the list of players in the current game
     */
    public List<Player> getCurrentGamePlayers() {

        return this.currentGame.getPlayers();

    }

    /**
     * Checks if the current game (the game that is in the waiting room state) contains the specified
     * player
     * @param player the player to check
     * @return true if the player is in the current game, false otherwise
     */
    public boolean currentGameContains(Player player) {

        return this.getCurrentGamePlayers().contains(player);

    }

    /**
     * Checks if the current game (the game that is in the waiting room state) contains the specified
     * player by its username
     * @param username the username of the player to check
     * @return true if the player with the specified username is in the current game, false otherwise
     */
    public boolean currentGameContains(String username) {

        return this.getCurrentGamePlayers().stream().anyMatch(p -> p.getUsername().equals(username));

    }

    /**
     * Starts the current game and creates a new Game to become the current game
     */
    public void startCurrentGame() {

        UUID currentGameUUID = currentGame.getUUID();

        gameUpdateManager.startGame(currentGameUUID);

        games.put(currentGameUUID, currentGame);
        this.currentGame = new Game(UUID.randomUUID());

    }

    /**
     * Adds a player to the current game (the game that is in the waiting room state)
     * @param player the player to add to the current game
     * @return true if the player was added, false if the current game already contains a
     * player with that username
     */
    public boolean addPlayerToCurrentGame(Player player) {

        if(this.currentGame.containsPlayer(player)) {

            return false;

        }

        this.currentGame.addPlayer(player);
        this.gameUpdateManager.playerJoined(player, currentGame.getUUID());
        return true;

    }

    /**
     * Removes a player from a game by its UUID
     * @param player the player to remove
     * @param gameUUID the UUID of the game to remove this player from
     */
    public void removePlayerFromGame(Player player, UUID gameUUID) {

        Game game = this.getGame(gameUUID);

        if(game == null) {
            return;
        }

        game.removePlayer(player);
        this.gameUpdateManager.playerLeft(player, gameUUID);

    }

    /**
     * Removes a player by its username from a game by its UUID
     * @param username the username of the player to remove
     * @param gameUUID the UUID of the game to remove this player from
     */
    public void removePlayerFromGame(String username, UUID gameUUID) {

        Game game = this.getGame(gameUUID);

        if(game == null) {
            return;
        }

        Player player = game.getPlayer(username);

        if(player == null) {
            return;
        }

        this.removePlayerFromGame(player, gameUUID);

    }

    /**
     * Removes a player from the current game (the game that is in the waiting room state)
     * @param player the player to remove
     */
    public void removePlayerFromCurrentGame(Player player) {

        this.removePlayerFromGame(player, this.currentGame.getUUID());

    }

    /**
     * Removes a player from the current game (the game that is in the waiting room state) by its
     * username
     * @param username the username of the player to remove
     */
    public void removePlayerFromCurrentGame(String username) {

        this.removePlayerFromGame(username, this.currentGame.getUUID());

    }

}
