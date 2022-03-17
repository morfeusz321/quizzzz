package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class LeaderboardCtrl {

    private ServerUtils server;
    private MainCtrl mainCtrl;

    private String goToScene;

    @FXML
    private ImageView backBtn;

    /**
     * Constructor for this controller
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public LeaderboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the back button
     */
    public void initialize() {
        backBtn.setImage(new Image("/client/img/back_btn.png"));
        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            mainCtrl.showMainScreen();
        });
    }

}
