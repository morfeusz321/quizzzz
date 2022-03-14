package client.utils;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class DynamicText {
    private final Text outTxt;

    private final double maxHeight;
    private final double middleY;
    private final double pixelsPerSize;
    private double currLineHeight;
    private final String fontFamily;

    /**
     * Create an instance of a dynamic text handler. This handles the resizing of the font for
     * a certain Text object.
     * @param outTxt The Text object which this class handles
     * @param maxHeight The maximal height the text should have
     * @param fontFamily The font family of the text
     */
    public DynamicText(Text outTxt, double maxHeight, String fontFamily) {
        this.outTxt = outTxt;
        this.maxHeight = maxHeight;
        this.fontFamily = fontFamily;
        currLineHeight = outTxt.getBoundsInLocal().getHeight();
        // Note: this requires the text when calling the constructor to be one line!
        middleY = outTxt.getY() + currLineHeight/2;
        // get correct "center" for centering text later on -> might be better to include a constructor
        // parameter for this
        pixelsPerSize = currLineHeight / outTxt.getFont().getSize();
    }

    /**
     * Sets the text of the Text object to a given string, while considering the resizing of the text (keeping the
     * height below the maximum height).
     * @param text The string to set the text to
     */
    public void setText(String text){
        outTxt.setText(text);
        double newHeight = outTxt.getBoundsInLocal().getHeight();
        if(newHeight > maxHeight){
            double currNumLines = newHeight / currLineHeight;
            double maxLineHeight = maxHeight / currNumLines;

            double maxFontSize = maxLineHeight / pixelsPerSize;
            outTxt.setFont(Font.font(fontFamily, maxFontSize));

            currLineHeight = maxLineHeight;
        }

        // center text vertically
        outTxt.setY(middleY - outTxt.getBoundsInLocal().getHeight()/2);
    }

    /**
     * Checks whether the given object is equal to this one
     * @param obj Object to compare to
     * @return Whether this object equals the given one
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generates hash code for this object
     * @return The hash code of this object
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns the string representation of this object
     * @return The string representation of this object
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
