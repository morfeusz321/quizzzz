/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    public void checkConstructorAndGetPoints() {
        Player p = new Player();
        assertEquals(p.getPoints(), 0);
    }

    @Test
    public void checkGetAndSetUsername() {
        Player p = new Player();
        p.setUsername("blabla");
        assertEquals(p.getUsername(), "blabla");
    }

    @Test
    public void checkIncreaseAndDecreasePoints() {
        Player p = new Player();
        p.setUsername("blabla");
        p.increasePoints(20);
        p.decreasePoints(5);
        assertEquals(p.getPoints(), 15);
    }

    @Test
    public void checkEqualsNull() {
        Player p = new Player();
        assertFalse(p.equals(null));
    }

    @Test
    public void checkEqualsTrue() {
        Player p = new Player();
        p.setUsername("blabla");
        Player b = new Player();
        b.setUsername("blabla");
        assertTrue(b.equals(p));
    }

    @Test
    public void checkEqualsFalse() {
        Player p = new Player();
        p.setUsername("blabla");
        Player b = new Player();
        b.setUsername("blabla");
        b.increasePoints(1);
        assertFalse(p.equals(b));
    }

    @Test
    void testHashCode() {
        var player = new Player();
        var player2 = new Player();
        assertEquals(player, player2);
        assertEquals(player.hashCode(), player2.hashCode());
    }

    @Test
    void testToString() {
        var player = new Player();
        assertEquals(ToStringBuilder.reflectionToString(player, MULTI_LINE_STYLE), player.toString());
    }
}