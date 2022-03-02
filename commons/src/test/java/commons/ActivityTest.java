package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    @Test
    void testEmptyConstructor()
    {
        var activity = new Activity();
        assertNotNull(activity);
    }

    @Test
    void testConstructor()
    {
        var activity = new Activity("1","/path/to/image/","Activity", 200);
        assertNotNull(activity);
        assertEquals("1", activity.id);
        assertEquals("/path/to/image/", activity.imagePath);
        assertEquals("Activity", activity.title);
        assertEquals(200, activity.consumption);

    }

    @Test
    void displayActivity() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        assertEquals("taking a shower for 2 minutes", activity.displayActivity());

    }

    @Test
    void testEquals() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var activity2 = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        assertEquals(activity, activity2);
    }

    @Test
    void testNotEquals() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var activity2 = new Activity("1","/path/to/image/","Taking a shower for 5 minutes", 200);
        assertNotEquals(activity, activity2);
    }

    @Test
    void testHashCode() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        var activity2 = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        assertEquals(activity, activity2);
        assertEquals(activity.hashCode(), activity2.hashCode());
    }

    @Test
    void testToString() {
        var activity = new Activity("1","/path/to/image/","Taking a shower for 2 minutes", 200);
        assertEquals(ToStringBuilder.reflectionToString(activity, MULTI_LINE_STYLE), activity.toString());
    }
}