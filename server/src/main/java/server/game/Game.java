package server.game;

import commons.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Game {

    private UUID uuid;

    private ConcurrentHashMap<String, Player> players;

    /**
     * Creates a new game
     * @param uuid the UUID for this new game
     */
    public Game(UUID uuid) {

        this.uuid = uuid;
        this.players = new ConcurrentHashMap<>();

    }

    /**
     * Returns this game's UUID
     * @return this game's UUID
     */
    public UUID getUUID() {

        return this.uuid;

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
     * Adds a player to this game
     * @param player the player that is joining
     */
    public void addPlayer(Player player) {

        this.players.put(player.getUsername(), player);

    }

    /**
     * Removes a player from this game
     * @param player the player that is leaving
     */
    public void removePlayer(Player player) {

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

}
