package client.utils;

import client.utils.mocks.MockGameUpdate;
import client.utils.mocks.MockLongPollThread;
import client.utils.mocks.MockStompSession;
import commons.gameupdate.GameUpdate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompSession;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class ServerUtilsTest {

    private ServerUtils serverUtils;

    private StompSession mockStompSession;
    private LongPollThread mockLongPollThread;

    private boolean flag = false;

    @BeforeEach
    public void setup() {

        this.serverUtils = new ServerUtils();

    }

    @AfterEach
    public void resetAll() {

        flag = false;

        setupMockStompSession((s) -> {

        });
        setupMockLongPollThread("", UUID.randomUUID(), (gu) -> {

        }, "", false);
        resetServerUtils();

    }

    private void setupMockStompSession(Consumer<String> mockReader) {

        this.mockStompSession = new MockStompSession(mockReader);

    }

    private void setupMockLongPollThread(String server, UUID gameUUID, Consumer<GameUpdate> consumer, String username, boolean isInGame) {

        this.mockLongPollThread = new MockLongPollThread(server, gameUUID, consumer, username, isInGame);

    }

    private void setupServerUtils(String SERVER, String WS_SERVER, StompSession session, UUID gameUUID, boolean isInGame, LongPollThread longPollThread) {

        try {

            Field serverField = ServerUtils.class.getDeclaredField("SERVER");
            serverField.setAccessible(true);
            serverField.set(null, SERVER);

            Field wsServerField = ServerUtils.class.getDeclaredField("WS_SERVER");
            wsServerField.setAccessible(true);
            wsServerField.set(null, WS_SERVER);

            Field sessionField = ServerUtils.class.getDeclaredField("session");
            sessionField.setAccessible(true);
            sessionField.set(serverUtils, session);

            Field gameUUIDField = ServerUtils.class.getDeclaredField("gameUUID");
            gameUUIDField.setAccessible(true);
            gameUUIDField.set(serverUtils, gameUUID);

            Field isInGameField = ServerUtils.class.getDeclaredField("isInGame");
            isInGameField.setAccessible(true);
            isInGameField.set(serverUtils, isInGame);

            Field longPollThreadField = ServerUtils.class.getDeclaredField("longPollThread");
            longPollThreadField.setAccessible(true);
            longPollThreadField.set(serverUtils, longPollThread);

        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void resetServerUtils() {

        setupServerUtils("", "", null, null, false, null);

    }

    @Test
    public void testDisconnect() {

        setupMockStompSession((s) -> fail());
        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);
        serverUtils.disconnect();

        setupMockStompSession((s) -> {
            if(s.equals("Disconnected")) {
                flag = true;
            }
        });
        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);
        ((MockStompSession) mockStompSession).setIsConnected(true);
        serverUtils.disconnect();
        if(!flag) fail();

    }

    @Test
    public void testRegisterForGameUpdates() {

        UUID uuid = UUID.randomUUID();

        setupMockStompSession((s) -> {
            if(s.equals("/topic/gameupdates/" + uuid)) {
                flag = true;
            }
        });
        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);

        serverUtils.registerForGameUpdates(uuid, (gu) -> {

        });

        if(!flag) fail();
        assertNotNull(mockStompSession.getSessionId());
        assertEquals("/topic/gameupdates/" + uuid, ((MockStompSession) mockStompSession).destination);
        assertTrue(((MockStompSession) mockStompSession).isConnected);

    }

    @Test
    public void testSendEmoji() {

        UUID uuid = UUID.randomUUID();
        MockGameUpdate mockGameUpdate = new MockGameUpdate("payload");

        setupMockStompSession((s) -> {
            String[] split = s.split(";");
            if(split.length != 2) fail();
            if(!split[0].equals("/game/emoji/" + uuid)) fail();
            if(!split[1].equals(mockGameUpdate.toString())) fail();
            flag = true;
        });
        setupServerUtils("", "", mockStompSession, uuid, false, mockLongPollThread);

        serverUtils.sendEmoji(mockGameUpdate);

        if(!flag) fail();

    }

    @Test
    public void testRegisterForWebsocketMessages() {

        setupMockStompSession((s) -> {
            if(s.equals("destination")) {
                flag = true;
            }
        });
        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);

        Method method = null;
        try {
            method = serverUtils.getClass().getDeclaredMethod("registerForWebsocketMessages", String.class, Class.class, Consumer.class);
        } catch (NoSuchMethodException e) {
            fail();
        }
        method.setAccessible(true);
        try {
            Object r = method.invoke(serverUtils, "destination", String.class, (Consumer<String>) s -> {

            });
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail();
        }

        if(!flag) fail();
        assertNotNull(mockStompSession.getSessionId());
        assertEquals("destination", ((MockStompSession) mockStompSession).destination);
        assertTrue(((MockStompSession) mockStompSession).isConnected);

    }

    @Test
    public void testGetServer() {

        setupServerUtils("SERVER", "", mockStompSession, null, false, mockLongPollThread);

        assertEquals("SERVER", ServerUtils.getServer());

    }

    @Test
    public void testGetImageURL() {

        String expected = "SERVER/api/img/24/test+image.png";

        setupServerUtils("SERVER/", "", mockStompSession, null, false, mockLongPollThread);

        assertEquals(expected, ServerUtils.getImageURL("24/test image.png"));

    }

    @Test
    public void testLeaveGame() {

        UUID uuid = UUID.randomUUID();

        setupMockStompSession((s) -> {
            if(s.equals("Disconnected")) {
                flag = true;
            }
        });
        ((MockStompSession) mockStompSession).setIsConnected(true);
        setupMockLongPollThread("", uuid, (gu) -> {

        }, "username", true);
        setupServerUtils("", "", mockStompSession, null, true, mockLongPollThread);
        String ret = serverUtils.leaveGame("username", uuid);

        assertFalse(serverUtils.getIsInTheGame());
        assertFalse(((MockLongPollThread) mockLongPollThread).isInGame());
        if(!flag) fail();
        assertEquals("", ret);

    }

    @Test
    public void testStartGame() {

        setupMockStompSession((s) -> {
            String[] split = s.split(";");
            if(split.length != 2) fail();
            if(!split[0].equals("/game/start")) fail();
            if(!split[1].equals("A game start has been requested.")) fail();
            flag = true;
        });
        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);

        serverUtils.startGame();

        if(!flag) fail();

    }

    @Test
    public void testChangeServerMalformed() {

        String address = "http://http://test.com/";
        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);

        assertThrows(IllegalArgumentException.class, () -> serverUtils.changeServer(address));

        String actual = "";
        try {
            Field serverField = ServerUtils.class.getDeclaredField("SERVER");
            serverField.setAccessible(true);
            actual = (String) serverField.get(null);
        } catch(NoSuchFieldException | IllegalAccessException e2) {
            fail();
        }

        assertEquals("", actual);

    }

    @Test
    public void testChangeServer() {

        String address = "something";
        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);

        try {
            serverUtils.changeServer(address);
        } catch(IllegalArgumentException e) {

            String actual = "";
            try {
                Field serverField = ServerUtils.class.getDeclaredField("SERVER");
                serverField.setAccessible(true);
                actual = (String) serverField.get(null);
            } catch(NoSuchFieldException | IllegalAccessException e2) {
                fail();
            }

            assertEquals("http://something/", actual);
            return;

        }

        // This address isn't valid so it should always throw an IllegalArgumentException
        fail();

    }

    @Test
    public void testSetGameUUID() {

        UUID uuid = UUID.randomUUID();

        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);
        serverUtils.setGameUUID(uuid);

        UUID actual = null;
        try {
            Field uuidField = ServerUtils.class.getDeclaredField("gameUUID");
            uuidField.setAccessible(true);
            actual = (UUID) uuidField.get(serverUtils);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail();
        }
        assertEquals(uuid, actual);

    }

    @Test
    public void testSetInGameTrue() {

        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);
        serverUtils.setInGameTrue();

        boolean actual = false;
        try {
            Field isInGameField = ServerUtils.class.getDeclaredField("isInGame");
            isInGameField.setAccessible(true);
            actual = (boolean) isInGameField.get(serverUtils);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail();
        }
        assertTrue(actual);

        assertTrue(serverUtils.getIsInTheGame());

    }

    @Test
    public void testGetIsInTheGame() {

        setupServerUtils("", "", mockStompSession, null, false, mockLongPollThread);
        assertFalse(serverUtils.getIsInTheGame());

        serverUtils.setInGameTrue();
        assertTrue(serverUtils.getIsInTheGame());

    }

}
