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

@Entity
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

    @SuppressWarnings("unused")
    public Activity() {

        // Empty constructor used by object mapper

    }

    public Activity(String id, String imagePath, String title, long consumption) {

        this.id = id;
        this.imagePath = imagePath;
        this.title = title;
        this.consumption = consumption;

    }

    public String displayActivity() {

        return Character.toLowerCase(title.charAt(0)) + title.substring(1);

    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
