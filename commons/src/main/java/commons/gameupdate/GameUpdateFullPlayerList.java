package commons.gameupdate;

import commons.Player;

import java.util.List;
import java.util.UUID;

public class GameUpdateFullPlayerList extends GameUpdate {

    private List<Player> playerList;
    private UUID gameUUID;

    private GameUpdateFullPlayerList() {

    }

    public GameUpdateFullPlayerList(List<Player> playerList, UUID gameUUID) {

        this.playerList = playerList;
        this.gameUUID = gameUUID;

    }

    public List<Player> getPlayerList() {

        return this.playerList;

    }

    public UUID getGameUUID() {

        return this.gameUUID;

    }

}
