package commons.gameupdate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameEmojiUpdateTest {

    @Test
    void getEmoji() {
        GameEmojiUpdate update = new GameEmojiUpdate("emoji", "player");
        Assertions.assertEquals("emoji", update.getEmoji());
    }

    @Test
    void getUsername() {
        GameEmojiUpdate update = new GameEmojiUpdate("emoji", "player");
        Assertions.assertEquals("player", update.getUsername());
    }

    @Test
    void testEquals() {
        GameEmojiUpdate update = new GameEmojiUpdate("notemoji", "player");
        GameEmojiUpdate update2 = new GameEmojiUpdate("emoji", "player");
        GameEmojiUpdate update3 = new GameEmojiUpdate("emoji", "notplayer");
        GameEmojiUpdate update4 = new GameEmojiUpdate("notemoji", "notplayer");
        GameEmojiUpdate update5 = new GameEmojiUpdate("emoji", "player");
        String notUpdate = "notUdpate";

        Assertions.assertEquals(update2, update5);
        Assertions.assertEquals(update2, update2);
        Assertions.assertNotEquals(update2, update);
        Assertions.assertNotEquals(update3, update2);
        Assertions.assertNotEquals(update2, update4);
        Assertions.assertNotEquals(notUpdate, update2);

    }

    @Test
    void testHashCode() {
        GameEmojiUpdate update = new GameEmojiUpdate("notemoji", "player");
        GameEmojiUpdate update2 = new GameEmojiUpdate("emoji", "player");
        GameEmojiUpdate update3 = new GameEmojiUpdate("emoji", "notplayer");
        GameEmojiUpdate update4 = new GameEmojiUpdate("notemoji", "notplayer");
        GameEmojiUpdate update5 = new GameEmojiUpdate("emoji", "player");
        String notUpdate = "notUdpate";

        Assertions.assertEquals(update2.hashCode(), update5.hashCode());
        Assertions.assertEquals(update2.hashCode(), update2.hashCode());
        Assertions.assertNotEquals(update2.hashCode(), update.hashCode());
        Assertions.assertNotEquals(update3.hashCode(), update2.hashCode());
        Assertions.assertNotEquals(update2.hashCode(), update4.hashCode());
        Assertions.assertNotEquals(notUpdate.hashCode(), update2.hashCode());
    }

    @Test
    void testToString() {
        GameEmojiUpdate update2 = new GameEmojiUpdate("lol", "player");
        assertTrue(update2.toString().contains(GameEmojiUpdate.class.getSimpleName()));
        assertTrue(update2.toString().contains("emoji"));
        assertTrue(update2.toString().contains("username"));
    }

}