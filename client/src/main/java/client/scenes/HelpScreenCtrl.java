package client.scenes;

import client.utils.AnimationUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpScreenCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final AnimationUtils animation;

    @FXML
    private ImageView backBtn;

    @FXML
    private AnchorPane anchorPane;

    /**
     * Constructor for help screen controller, which controls the interaction of the help screen
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public HelpScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.animation = new AnimationUtils();
    }

    /**
     * Initializes the event handlers and the image of the back button
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> fadeOutHelp("main"));
        backBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> backBtn.setEffect(hover));
        backBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> backBtn.setEffect(null));
        backBtn.setImage(new Image("/client/img/back_btn.png"));
    }

    /**
     * goes to the given scene and does fading animation
     * @param nextScene
     */
    public void fadeOutHelp(String nextScene){
        animation.fadeOut(anchorPane, mainCtrl, nextScene);
    }

    /**
     * when entering the scene it does fading animation
     */
    public void fadeInHelp(){
        animation.fadeIn(anchorPane);
    }
}
