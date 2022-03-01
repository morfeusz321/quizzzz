package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class DynamicTextController {

    private final MainCtrl mainCtrl;

    @FXML
    private TextArea inTxt;

    @FXML
    private Text outTxt;

    @Inject
    public DynamicTextController(MainCtrl mainCtrl) {

        this.mainCtrl = mainCtrl;

    }

    public void onSetTextButtonClick() {

        String txt = inTxt.getText();
        outTxt.setText(txt);

    }

}
