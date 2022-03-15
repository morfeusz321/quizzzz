package client.utils;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Objects;

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
     * @param txt The Text object which this class handles
     * @param maxHeight The maximal height the text should have
     */
    public DynamicText(Text txt, double maxHeight, double fontSize, String fontFamily) {
        this.outTxt = txt;
        this.maxHeight = maxHeight;
        this.fontFamily = fontFamily;
        outTxt.setFont(new Font(fontFamily,fontSize));
        currLineHeight = outTxt.getBoundsInLocal().getHeight();
        // Note: this requires the text when calling the constructor to be one line!
        middleY = outTxt.getY() + currLineHeight/2;
        // get correct "center" for centering text later on -> might be better to include a constructor
        // parameter for this
        pixelsPerSize = currLineHeight / fontSize;
    }

    /**
     * Sets the text of the Text object to a given string, while considering the resizing of the text (keeping the
     * height below the maximum height).
     * @param text The string to set the text to
     */
    public void setText(String text, int fontSize){
        outTxt.setText(text);
        outTxt.setFont(new Font(fontFamily, fontSize));
        double newHeight = outTxt.getBoundsInLocal().getHeight();
        if(newHeight > maxHeight*0.9){
            double currNumLines = newHeight / currLineHeight;
            double maxLineHeight = maxHeight / currNumLines;

            double maxFontSize = maxLineHeight / pixelsPerSize;
            outTxt.setFont(new Font (fontFamily,maxFontSize));

        }

    }

    /**
     * Checks whether the given object is equal to this one
     * @param o Object to compare to
     * @return Whether this object equals the given one
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicText that = (DynamicText) o;
        return Double.compare(that.maxHeight, maxHeight) == 0 && Double.compare(that.middleY, middleY) == 0 && Double.compare(that.pixelsPerSize, pixelsPerSize) == 0 &&
                Double.compare(that.currLineHeight, currLineHeight) == 0 && outTxt.getText().equals(that.outTxt.getText()) && fontFamily.equals(that.fontFamily);
    }


    /**
     * Generates hash code for this object
     * @return The hash code of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(outTxt.getText(), maxHeight, middleY, pixelsPerSize, currLineHeight, fontFamily);
    }

    /**
     * Returns the string representation of this object
     * @return The string representation of this object
     */
    @Override
    public String toString() {
        return "DynamicText{" +
                "outTxt=" + outTxt +
                ", maxHeight=" + maxHeight +
                ", middleY=" + middleY +
                ", pixelsPerSize=" + pixelsPerSize +
                ", currLineHeight=" + currLineHeight +
                ", fontFamily='" + fontFamily + '\'' +
                '}';
    }

}
