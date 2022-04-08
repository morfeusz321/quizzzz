package client.utils;

/*
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
*/

/**
 * These tests have passed (last confirmed 07.04.2022)
 *
 * They are commented out, because they do not run on the build server
 * A possible cause has been found (namely, missing libraries
 * on the build server), however, it is not likely that those
 * will be added any time soon
 */
class DynamicTextTest {

    /*
    @Test
    void setText() {
        String text = "Very very Very very Very very Very very Very very Very very Very very Very very Very very Very very Very very long question";
        int originalFontSize = 20;
        Text newText = new Text(text);
        newText.setFont(new Font(originalFontSize));
        DynamicText dt = new DynamicText(newText, 25, newText.getFont().getSize(), "System Bold");
        dt.setText(originalFontSize);
        assertNotEquals(originalFontSize, newText.getFont().getSize());
    }

    @Test
    void testEquals() {
        DynamicText dt1 = new DynamicText(new Text(""), 20, 10, "Arial");
        DynamicText dt2 = new DynamicText(new Text(""), 20, 10, "Arial");
        assertEquals(dt1, dt2);
    }

    @Test
    void testHashCode() {
        DynamicText dt1 = new DynamicText(new Text(""), 20, 10, "Arial");
        DynamicText dt2 = new DynamicText(new Text(""), 20, 10, "Arial");
        assertNotEquals(dt1.hashCode(), dt2.hashCode());
    }

    @Test
    void testToString() {
        DynamicText dt1 = new DynamicText(new Text(""), 20, 10, "Arial");
        DynamicText dt2 = new DynamicText(new Text(""), 20, 10, "Arial");
        assertNotEquals(dt1.toString(), dt2.toString());
    }
     */

}
