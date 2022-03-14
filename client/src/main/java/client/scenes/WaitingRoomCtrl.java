package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CommonUtils;
import javafx.fxml.FXML;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class WaitingRoomCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private final CommonUtils utils;

    @FXML
    private ImageView backBtn;

    /**
     * Creates a WaitingRoomCtrl, which controls the display/interaction of the waiting room.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils Common utilities (for server- and client-side)
     */
    @Inject
    public WaitingRoomCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.utils = utils;
    }

    /**
     * Initializes the scene, i.e. handles initialization, images
     */
    public void initialize(){
        showImages();
        initializeBackButtonHandlers();
    }

    /**
     * Loads all images, i.e. initializes the images of all ImageView objects
     */
    public void showImages(){
        backBtn.setImage(new Image("/client/img/back_btn.png"));
    }

    /**
     * Initializes the event handlers of the back button
     */
    private void initializeBackButtonHandlers() {
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> mainCtrl.showOverview());
        // TODO: when the menu screen is added, modify this
        backBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> backBtn.setEffect(hover));
        backBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> backBtn.setEffect(null));
    }
}
