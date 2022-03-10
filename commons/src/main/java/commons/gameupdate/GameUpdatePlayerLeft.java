package commons.gameupdate;

import commons.Player;

public class GameUpdatePlayerLeft extends GameUpdate {

    private Player player;

    private GameUpdatePlayerLeft() {

    }

    public GameUpdatePlayerLeft(Player player) {

        this.player = player;

    }

    public Player getPlayer() {

        return this.player;

    }

}
