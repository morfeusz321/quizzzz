package commons.gameupdate;

import commons.Player;

public class GameUpdatePlayerJoined extends GameUpdate {

    private Player player;

    private GameUpdatePlayerJoined() {

    }

    public GameUpdatePlayerJoined(Player player) {

        this.player = player;

    }

    public Player getPlayer() {

        return this.player;

    }

}
