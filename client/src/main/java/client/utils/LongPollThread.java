package client.utils;

import commons.gameupdate.GameUpdate;
import commons.gameupdate.GameUpdateGameFinished;
import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;

import java.util.UUID;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class LongPollThread extends Thread {

    private String server;
    private UUID gameUUID;
    private Consumer<GameUpdate> consumer;
    private String username;
    private boolean isInGame;

    /**
     * Creates LongPollThread instance with specified injections
     * @param server name of the server that you will send requests to
     * @param gameUUID uuid of the current game
     * @param consumer consumer to handle upcoming messages
     * @param username username of the current player
     * @param isInGame boolean value which says whether player is currently in the game
     */
    public LongPollThread(String server, UUID gameUUID, Consumer<GameUpdate> consumer, String username, boolean isInGame) {

        this.server = server;
        this.gameUUID = gameUUID;
        this.consumer = consumer;
        this.username = username;
        this.isInGame = isInGame;

    }

    /**
     * Starts long pulling requests
     */
    @Override
    public void run() {

        registerForGameLoop(consumer, username);

    }

    /**
     * Registers for the game loop updates with the current stored game UUID, and sends
     * all incoming game loop updates to the provided consumer. The long poll loop is automatically
     * cancelled upon leaving the game by clicking the back button or closing the window, and it is guaranteed
     * by this method that no further updates will be accepted by the provided consumer after leaving the game.
     * @param consumer the consumer that accepts incoming game loop updates
     */
    public void registerForGameLoop(Consumer<GameUpdate> consumer, String username) {

        GameUpdate ret = null;
        while(!(ret instanceof GameUpdateGameFinished) && isInGame) {
            ret = ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/game/")
                    .queryParam("gameID", gameUUID.toString())
                    .queryParam("username", username)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(GameUpdate.class);
            if(isInGame) consumer.accept(ret);
        }

    }

}
