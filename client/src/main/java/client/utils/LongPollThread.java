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

    /**
     * jfdfaj;dlskfj
     * @param server
     * @param gameUUID
     * @param consumer
     * @param username
     */
    public LongPollThread(String server, UUID gameUUID, Consumer<GameUpdate> consumer, String username) {

        this.server = server;
        this.gameUUID = gameUUID;
        this.consumer = consumer;
        this.username = username;

    }

    /**
     * JavaDoc
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
        while(!(ret instanceof GameUpdateGameFinished) && true) {
            ret = ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/game/")
                    .queryParam("gameID", gameUUID.toString())
                    .queryParam("username", username)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(GameUpdate.class);
            if(true) consumer.accept(ret);
        }

    }

}
