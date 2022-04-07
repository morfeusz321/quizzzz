package client.utils.mocks;

import client.utils.LongPollThread;
import commons.gameupdate.GameUpdate;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Consumer;

public class MockLongPollThread extends LongPollThread {


    /**
     * Creates LongPollThread instance with specified injections
     *
     * @param server   name of the server that you will send requests to
     * @param gameUUID uuid of the current game
     * @param consumer consumer to handle upcoming messages
     * @param username username of the current player
     * @param isInGame boolean value which says whether player is currently in the game
     */
    public MockLongPollThread(String server, UUID gameUUID, Consumer<GameUpdate> consumer, String username, boolean isInGame) {
        super(server, gameUUID, consumer, username, isInGame);
    }

    /**
     * Registers for the game loop updates with the current stored game UUID, and sends
     * all incoming game loop updates to the provided consumer. The long poll loop is automatically
     * cancelled upon leaving the game by clicking the back button or closing the window, and it is guaranteed
     * by this method that no further updates will be accepted by the provided consumer after leaving the game.
     * @param consumer the consumer that accepts incoming game loop updates
     */
    @Override
    public void registerForGameLoop(Consumer<GameUpdate> consumer, String username) {

        consumer.accept(new MockGameUpdate(username));

    }

    /**
     * Returns this MockLongPollThread's isInGame status via reflection
     * @return whether this MockLongPollThread's isInGame is set
     */
    public boolean isInGame() {

        try {
            Field isInGameField = LongPollThread.class.getDeclaredField("isInGame");
            isInGameField.setAccessible(true);
            return (boolean) isInGameField.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }

    }

}
