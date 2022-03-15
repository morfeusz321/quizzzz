package client.utils;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTextTest {

    @Test
    void setText() {
        String text = "Very very Very very Very very Very very Very very Very very Very very Very very Very very Very very Very very long question";
        int originalFontSize = 20;
        Text newText = new Text(text);
        newText.setFont(new Font(originalFontSize));
        DynamicText dt = new DynamicText(newText, 25, newText.getFont().getSize(), "System Bold");
        dt.setText(text, originalFontSize);
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
        assertEquals(dt1.hashCode(), dt2.hashCode());
    }

    @Test
    void testToString() {
        DynamicText dt1 = new DynamicText(new Text(""), 20, 10, "Arial");
        DynamicText dt2 = new DynamicText(new Text(""), 20, 10, "Arial");
        assertEquals(dt1.toString(), dt2.toString());
    }
}
