package server.game;

import commons.GameType;
import commons.Player;
import org.apache.commons.lang3.builder.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import server.api.QuestionController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Component
@Scope("prototype")
public class Game {

    private UUID uuid;
    private GameType gameType;

    private ConcurrentHashMap<String, Player> players;

    @HashCodeExclude
    @EqualsExclude
    @ToStringExclude
    private GameUpdateManager gameUpdateManager;

    @HashCodeExclude
    @EqualsExclude
    @ToStringExclude
    private QuestionController questionController;

    /**
     * Creates a new game
     * @param gameUpdateManager the game update manager used by this game to send messages to the client
     * @param questionController the question generator (so that the server does not have to send API requests to itself)
     */
    public Game(GameUpdateManager gameUpdateManager, QuestionController questionController) {

        // TODO: not sure if the server should send requests for questions to itself via the API mapping (this does not
        //  really make sense, and would create overhead), so I included the QuestionController as a parameter for now.
        //  If this is not the best way of handling it, we would have to change this here.

        this.gameUpdateManager = gameUpdateManager;
        this.questionController = questionController;
        this.players = new ConcurrentHashMap<>();

    }

    /**
     * Sets this game's UUID
     * @param uuid the UUID for this game
     */
    public void setUUID(UUID uuid) {

        this.uuid = uuid;

    }

    /**
     * Returns this game's UUID
     * @return this game's UUID
     */
    public UUID getUUID() {

        return this.uuid;

    }

    /**
     * Sets this game's game type
     * @param gameType the game type for this game
     */
    public void setGameType(GameType gameType) {

        this.gameType = gameType;

    }

    /**
     * Returns this game's game type
     * @return this game's game type
     */
    public GameType getGameType() {

        return this.gameType;

    }

    /**
     * Returns a list of all players in this game
     * @return all players in this game
     */
    public List<Player> getPlayers() {

        List<Player> list = new ArrayList<>();
        list.addAll(players.values());

        return list;

    }

    /**
     * Returns a player in this game with the specified username
     * @param username the username of the player to retrieve
     * @return the player with that username if it can be found, or null if it can't
     */
    public Player getPlayer(String username) {

        return this.players.getOrDefault(username, null);

    }

    /**
     * Adds a player to this game. Note that the Game assumes that it has been confirmed already that a
     * player with this username is not yet in the Game, and that this is *not* checked again
     * in this method
     * @param player the player that is joining
     */
    protected void addPlayer(Player player) {

        this.players.put(player.getUsername(), player);

    }

    /**
     * Removes a player from this game
     * @param player the player that is leaving
     */
    public void removePlayer(Player player) {

        if(player == null) return;

        this.players.remove(player.getUsername());

    }

    /**
     * Removes a player from this game by username
     * @param username the username of the player that is leaving
     */
    public void removePlayer(String username) {

        this.players.remove(username);

    }

    /**
     * Checks whether the specified player is in this game
     * @param player the player to check
     * @return true if the player is in this game, false otherwise
     */
    public boolean containsPlayer(Player player) {

        return this.players.containsKey(player.getUsername());

    }

    /**
     * Checks whether the specified player is in this game by its username
     * @param username the username of the player to check
     * @return true if the player with the specified username is in this game, false otherwise
     */
    public boolean containsPlayer(String username) {

        return this.players.containsKey(username);

    }

    /**
     * Checks if 2 game objects are equal
     * @param obj the object that will be compared
     * @return true or false, whether the objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generate a hash code for this object
     * @return hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Creates a formatted string for this object
     * @return a formatted string in multi line style
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
