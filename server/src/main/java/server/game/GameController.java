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

    public GameController(GameUpdateManager gameUpdateManager) {

        this.games = new ConcurrentHashMap<>();
        this.currentGame = new Game(UUID.randomUUID());
        this.gameUpdateManager = gameUpdateManager;

    }

    public Game getCurrentGame() {

        return this.currentGame;

    }

    public UUID getCurrentGameUUID() {

        return this.currentGame.getUUID();

    }

    public Game getGame(UUID uuid) {

        if(this.games.containsKey(uuid)) {
            return this.games.get(uuid);
        } else if(this.currentGame.getUUID().equals(uuid)) {
            return this.currentGame;
        } else {
            return null;
        }

    }

    public List<Player> getCurrentGamePlayers() {

        return this.currentGame.getPlayers();

    }

    public boolean currentGameContains(Player player) {

        return this.getCurrentGamePlayers().contains(player);

    }

    public boolean currentGameContains(String username) {

        return this.getCurrentGamePlayers().stream().anyMatch(p -> p.getUsername().equals(username));

    }

    public void startCurrentGame() {

        UUID currentGameUUID = currentGame.getUUID();

        gameUpdateManager.startGame(currentGameUUID);

        games.put(currentGameUUID, currentGame);
        this.currentGame = new Game(UUID.randomUUID());

    }

    public boolean addPlayerToCurrentGame(Player player) {

        if(this.currentGame.containsPlayer(player)) {

            return false;

        }

        this.currentGame.addPlayer(player);
        this.gameUpdateManager.playerJoined(player, currentGame.getUUID());
        return true;

    }

    public void removePlayerFromGame(Player player, UUID gameUUID) {

        Game game = this.getGame(gameUUID);

        if(game == null) {
            return;
        }

        game.removePlayer(player);
        this.gameUpdateManager.playerLeft(player, gameUUID);

    }

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

    public void removePlayerFromCurrentGame(Player player) {

        this.removePlayerFromGame(player, this.currentGame.getUUID());

    }

    public void removePlayerFromCurrentGame(String username) {

        this.removePlayerFromGame(username, this.currentGame.getUUID());

    }

}
