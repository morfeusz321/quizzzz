package commons.gameupdate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A GameUpdate represents a change in the state of the current game, and is sent to clients to inform them
 * of this change. For example, it could indicate a player joining or leaving, or the fact that the game is
 * starting.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class GameUpdate {



}
