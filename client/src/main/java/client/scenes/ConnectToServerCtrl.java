package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

public class ConnectToServerCtrl {

    private ServerUtils server;
    private MainCtrl mainCtrl;

    private String goToScene;

    @FXML
    private ImageView backBtn;

    @FXML
    private TextField serverAddress;

    /**
     * Constructor for this controller
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public ConnectToServerCtrl(ServerUtils server, MainCtrl mainCtrl) {
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

    /**
     * Updates the server address input field to show the current server address prefill
     */
    public void updateServerAddressPrefill() {

        serverAddress.setText(mainCtrl.getSavedServerAddressPrefill());
        serverAddress.selectAll();
        serverAddress.requestFocus();

    }

    /**
     * Sets which scene this controller should show next after successfully connecting to a server. The provided
     * String should be given by NextSceneController.class.getName()
     * @param goToScene the next scene to show after connecting to a server by the class name of its controller
     */
    public void setGoToScene(String goToScene) {
        this.goToScene = goToScene;
    }

    /**
     * This method is called upon clicking the connect button. It attempts to connect
     * to the specified server, and continues to the next screen if it succeeds, or
     * stays on this screen if it fails. In any case, the server address prefill is
     * updated.
     */
    public void connect() {

        mainCtrl.setServerAddressPrefill(serverAddress.getText());

        try {
            server.changeServer(serverAddress.getText());
        } catch(IllegalArgumentException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        if(goToScene.equals(AdminCtrl.class.getName())) {

            mainCtrl.showAdminServerConfirmed();

        } else if(goToScene.equals(LeaderboardCtrl.class.getName())) {

            mainCtrl.showLeaderboardServerConfirmed();

        } else {

            mainCtrl.showMainScreen();

        }

    }

}
