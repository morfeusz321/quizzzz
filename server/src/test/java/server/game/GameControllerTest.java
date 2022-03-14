package server.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class GameControllerTest {

    private GameUpdateManager gameUpdateManager;
    private FakeSimpMessagingTemplate simpMessagingTemplate;

    private GameController gameController;

    @BeforeEach
    public void setup() {

        this.simpMessagingTemplate = new FakeSimpMessagingTemplate();
        this.gameUpdateManager = new GameUpdateManager(this.simpMessagingTemplate);

        this.gameController = new GameController(this.gameUpdateManager);

    }

    @Test
    public void testConstructor() {

        assertNotNull(gameController);
        assertNotNull(gameController.getCurrentGame());

        try {

            Field gamesField = GameController.class.getDeclaredField("games");
            gamesField.setAccessible(true);
            assertNotNull(gamesField.get(gameController));

            Field gameUpdateManagerField = GameController.class.getDeclaredField("gameUpdateManager");
            gameUpdateManagerField.setAccessible(true);
            assertNotNull(gameUpdateManagerField.get(gameController));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail();
        }

    }

}
