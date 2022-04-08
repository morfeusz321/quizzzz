package commons.gameupdate;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GameEmojiUpdate extends GameUpdate {

    private String emoji;
    private String username;

    /**
     * Creates instance of the GameEmojiUpdate
     *
     * @param emoji    id of the emoji that was sent
     * @param username name of the user who sent the emoji
     */
    public GameEmojiUpdate(String emoji, String username) {
        this.emoji = emoji;
        this.username = username;
    }

    /**
     * Empty constructor for JSON parsing
     */
    public GameEmojiUpdate() {
    }

    /**
     * Returns the id of the sent emoji
     *
     * @return return sent emoji id
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * Returns name of the user who sent the id
     *
     * @return name of the user who sent id
     */
    public String getUsername() {
        return username;
    }

    /**
     * Equals method of GameEmojiUpdate
     *
     * @param o object we want to compare to
     * @return boolean representing equality of o and GameEmojiUpdate on which it was called
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        if(o == null || getClass() != o.getClass()) return false;

        GameEmojiUpdate that = (GameEmojiUpdate) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(emoji, that.emoji).append(username, that.username).isEquals();
    }

    /**
     * Hash function for GameEmojiUpdate
     *
     * @return hash of the class instance
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(emoji).append(username).toHashCode();
    }

    /**
     * ToString method of the GameEmojiUpdate
     *
     * @return string description of the class instance
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("emoji", emoji)
                .append("username", username)
                .toString();
    }

}

