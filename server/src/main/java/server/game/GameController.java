package server.game;

import commons.GameType;
import commons.Player;
import commons.Score;
import commons.gameupdate.GameUpdate;
import commons.gameupdate.GameUpdateFullPlayerList;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import server.api.ScoreController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameController implements ApplicationContextAware {

    private ConcurrentHashMap<UUID, Game> runningGames;
    private Game currentGame;
    private GameUpdateManager gameUpdateManager;
    private ScoreController scoreController;

    private ApplicationContext context;

    /**
     * Sets the application context that this class uses to get Game Beans
     * @param applicationContext the application context
     * @throws BeansException if thrown by application context methods
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * Instantiates a game controller
     * @param gameUpdateManager the update manager for WebSocket messages
     * @param scoreController the score controller to save scores
     */
    public GameController(GameUpdateManager gameUpdateManager, ScoreController scoreController) {

        this.runningGames = new ConcurrentHashMap<>();
        this.gameUpdateManager = gameUpdateManager;
        this.scoreController = scoreController;

    }

    /**
     * Initializes the current game
     */
    @PostConstruct
    public void init() {

        this.currentGame = context.getBean(Game.class);
        this.currentGame.setUUID(UUID.randomUUID());
        this.currentGame.setGameType(GameType.MULTIPLAYER);

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

        if(this.runningGames.containsKey(uuid)) {
            return this.runningGames.get(uuid);
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

        return this.currentGame.containsPlayer(player);

    }

    /**
     * Checks if the current game (the game that is in the waiting room state) contains the specified
     * player by its username
     * @param username the username of the player to check
     * @return true if the player with the specified username is in the current game, false otherwise
     */
    public boolean currentGameContains(String username) {

        return this.currentGame.containsPlayer(username);

    }

    /**
     * Starts the current game and creates a new Game to become the current game
     */
    public void startCurrentGame() {

        UUID currentGameUUID = currentGame.getUUID();

        this.currentGame.start();
        runningGames.put(currentGameUUID, currentGame);

        this.currentGame = context.getBean(Game.class);
        this.currentGame.setUUID(UUID.randomUUID());
        this.currentGame.setGameType(GameType.MULTIPLAYER);

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

        if(player == null) return;

        Game game = this.getGame(gameUUID);

        if(game == null) {
            return;
        }

        game.removePlayer(player);
        this.gameUpdateManager.playerLeft(player, gameUUID);

        // Check if all players left, in that case stop the game. It also has to be checked whether this is the
        // current game in the waiting room, if that is the case, the game does not have to be stopped as it has
        // not started yet.
        if(game != currentGame && game.getPlayers().size() == 0){
            stopGame(game);
        }

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

        // Check if all players left, in that case stop the game. It also has to be checked whether this is the
        // current game in the waiting room, if that is the case, the game does not have to be stopped as it has
        // not started yet.
        if(game != currentGame && game.getPlayers().size() == 0){
            stopGame(game);
        }

    }

    /**
     * Stops a game (removes it from the runningGames, and handles saving the scores)
     * @param game the game to stop
     */
    public void stopGame(Game game) {
        // TODO: test this method, this has not been properly tested yet
        if(game == null || !runningGames.containsKey(game.getUUID())) {
            return;
        }
        runningGames.remove(game.getUUID());
        // Check if the game was stopped before it actually ended, in that case only interrupt the thread, otherwise
        // save all the scores.
        if(game.isDone()){
            // If the game ended after 20 questions, save all players scores
            List<Player> players = game.getPlayers();
            for(Player p: players){
                scoreController.addScore(p.getUsername(), p.getPoints());
            }
        }
        // Interrupt the game thread
        game.interrupt();
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

    /**
     * Creates a new single player game containing the provided player and starts it after
     * a small delay. This delay is necessary because, when the client sends its POST request
     * to join a single player game, this method will be executed pretty much immediately. However,
     * at that point, the client has only just received its Game UUID in the POST request response,
     * and still has to subscribe to that game's WebSocket topic. If there were no delay, this method
     * would send the game starting message to the WS topic before the player could even subscribe.
     * @param player the player for the single player game
     */
    public GameUpdate createSinglePlayerGame(Player player) {

        UUID uuid = UUID.randomUUID();
        Game singlePlayerGame = context.getBean(Game.class);
        singlePlayerGame.setUUID(uuid);
        singlePlayerGame.setGameType(GameType.SINGLEPLAYER);

        singlePlayerGame.addPlayer(player);

        this.runningGames.put(uuid, singlePlayerGame);

        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                singlePlayerGame.start();
            }
        }, 1500);

        return new GameUpdateFullPlayerList(singlePlayerGame.getPlayers(), singlePlayerGame.getUUID());

    }

    /**
     * retrieve the database
     * @return score database
     */
    public ScoreController getScoreController(String gameID) {
        // if the gameID is -1 the user is not in the middle of the game so the scores dont need to be saved
        if(!Objects.equals(gameID, "-1")){
            saveScores(gameID);
        }
        return scoreController;
    }

    /**
     * when the leaderboard is supposed to be shown the scores from that game have to be stored to the database
     * @param gameID
     */
    private void saveScores(String gameID){
        UUID id = UUID.fromString(gameID);
        Game game = getGame(id);
        ConcurrentHashMap<String, Score> leaderboard = game.getLeaderboard();
        List<Player> players = game.getPlayers();
        for(Player p: players){
            String username = p.getUsername();
            Score score = leaderboard.get(username);
            scoreController.addScore(username, score.getScore());
        }
    }
}
