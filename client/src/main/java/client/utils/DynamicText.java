package client.utils;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class DynamicText {

    private final Text outTxt;

    private final double maxHeight;
    private final double middleY;
    private final double pixelsPerSize;
    private final double currLineHeight;
    private final String fontFamily;

    /**
     * Create an instance of a dynamic text handler. This handles the resizing of the font for
     * a certain Text object.
     *
     * @param txt        The Text object which this class handles
     * @param maxHeight  The maximal height the text should have
     * @param fontSize   The size of the font of the text
     * @param fontFamily The family of the font of the text
     */
    public DynamicText(Text txt, double maxHeight, double fontSize, String fontFamily) {
        this.outTxt = txt;
        this.maxHeight = maxHeight;
        this.fontFamily = fontFamily;
        outTxt.setFont(new Font(fontFamily, fontSize));
        currLineHeight = outTxt.getBoundsInLocal().getHeight();
        // Note: this requires the text when calling the constructor to be one line!
        middleY = outTxt.getY() + currLineHeight / 2;
        // get correct "center" for centering text later on -> might be better to include a constructor
        // parameter for this
        pixelsPerSize = currLineHeight / fontSize;
    }

    /**
     * Sets the text of the Text object to a given string, while considering the resizing of the text (keeping the
     * height below the maximum height).
     *
     * @param fontSize Size of the font (maximum it can set it to, while keeping the bounds)
     */
    public void setText(int fontSize) {
        outTxt.setFont(new Font(fontFamily, fontSize));
        double newHeight = outTxt.getBoundsInLocal().getHeight();
        if(newHeight > maxHeight * 0.9) {
            double currNumLines = newHeight / currLineHeight;
            double maxLineHeight = maxHeight / currNumLines;

            double maxFontSize = maxLineHeight / pixelsPerSize;
            outTxt.setFont(new Font(fontFamily, maxFontSize));
        }
        outTxt.setY(middleY - outTxt.getBoundsInLocal().getHeight() / 2 * 0.8);

    }

    /**
     * Checks whether the given object is equal to this one
     *
     * @param o Object to compare to
     * @return Whether this object equals the given one
     */
    @Override
    public boolean equals(Object o) {
        // Kept this equals method and did not use the EqualsBuilder because Text has no equals method.
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        DynamicText that = (DynamicText) o;
        return Double.compare(that.maxHeight, maxHeight) == 0 && Double.compare(that.middleY, middleY) == 0 && Double.compare(that.pixelsPerSize, pixelsPerSize) == 0 &&
                Double.compare(that.currLineHeight, currLineHeight) == 0 && outTxt.getText().equals(that.outTxt.getText()) && fontFamily.equals(that.fontFamily);
    }


    /**
     * Generates hash code for this object
     *
     * @return The hash code of this object
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns the string representation of this object
     *
     * @return The string representation of this object
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
