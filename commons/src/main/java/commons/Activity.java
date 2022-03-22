package commons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity(name = "activity")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity {

    @Id
    public String id;

    @Column(columnDefinition="VARCHAR(255)")
    @JsonProperty("image_path")
    public String imagePath;

    public String title;

    @JsonProperty("consumption_in_wh")
    public long consumption;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    public Activity() {

    }

    /**
     * Creates an activity object
     * @param id of the activity
     * @param imagePath path where the image is located
     * @param title of the activity
     * @param consumption energy required for the activity
     */
    public Activity(String id, String imagePath, String title, long consumption) {

        this.id = id;
        this.imagePath = imagePath;
        this.title = title;
        this.consumption = consumption;

    }

    /**
     * Creates a form of the activity title that can be used in a sentence
     * @return the activity title with the first letter guaranteed to be lowercase so that it can be used in a sentence
     */
    public static String displayActivity(String title) {
        return Character.toLowerCase(title.charAt(0)) + title.substring(1);
    }

    /**
     * Checks if 2 activity objects are equal
     * @param obj the object that will be compared
     * @return true or false, whether the objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generate a hash code for this object
     * @return hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Creates a formatted string for this object
     * @return a formatted string in multi line style
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
