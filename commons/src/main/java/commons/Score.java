package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Score {
    @Id
    public String username;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private Score() {
        // for object mappers
    }

    /**
     * Creates a Score with the given username and score
     * @param username the username for this Score
     * @param score the score of that user
     */
    public Score(String username) {
        this.username = username;
    }


    /**
     * Checks if two score objects are equal
     * @param obj the object to which this one will be compared
     * @return true, if the objects are equal, else false
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generates the hashCode for this object
     * @return the hashcode for this object
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Generates a string representation of this object
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
