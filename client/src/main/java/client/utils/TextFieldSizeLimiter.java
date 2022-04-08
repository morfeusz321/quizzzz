package client.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * This listener limits the maximum input size of a TextField. It should be added as a listener
 * to a TextField's length property. Every time its length changes (by having a new character typed
 * in), this listener will ensure that, if the new length is greater than the provided maximum length,
 * the newly typed in character is not added to the TextField contents.
 */
public class TextFieldSizeLimiter implements ChangeListener<Number> {

    private final TextField textField;
    private final int sizeLimit;

    /**
     * Creates a new text field size limiter
     *
     * @param textField the TextField whose size should be limited
     * @param sizeLimit the desired size limit (>= 0)
     */
    public TextFieldSizeLimiter(TextField textField, int sizeLimit) {

        if(sizeLimit < 0) sizeLimit = 0;

        this.textField = textField;
        this.sizeLimit = sizeLimit;

    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

        if(newValue.intValue() > sizeLimit) {

            if(textField.getText().length() > sizeLimit) {

                textField.setText(textField.getText().substring(0, sizeLimit));

            }

        }

    }

}
