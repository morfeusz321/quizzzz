package server.game;

import commons.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Game {

    private UUID uuid;

    private ConcurrentHashMap<String, Player> players;

    public Game(UUID uuid) {

        this.uuid = uuid;
        this.players = new ConcurrentHashMap<>();

    }

    public UUID getUUID() {

        return this.uuid;

    }

    public List<Player> getPlayers() {

        List<Player> list = new ArrayList<>();
        list.addAll(players.values());

        return list;

    }

    public Player getPlayer(String username) {

        return this.players.getOrDefault(username, null);

    }

    public void addPlayer(Player player) {

        this.players.put(player.getUsername(), player);

    }

    public void removePlayer(Player player) {

        this.players.remove(player.getUsername());

    }

    public void removePlayer(String username) {

        this.players.remove(username);

    }

    public boolean containsPlayer(Player player) {

        return this.players.containsKey(player.getUsername());

    }

    public boolean containsPlayer(String username) {

        return this.players.containsKey(username);

    }

}
