package client.scenes;

import client.utils.AnimationUtils;
import client.utils.ModalFactory;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class ConnectToServerCtrl {

    private ServerUtils server;
    private ModalFactory modalFactory;
    private MainCtrl mainCtrl;
    private AnimationUtils animation;

    private String goToScene;

    @FXML
    private ImageView backBtn;

    @FXML
    private TextField serverAddress;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button connect;

    /**
     * Constructor for this controller
     * @param server Utilities for communicating with the server (API endpoint)
     * @param modalFactory the modal factory to use
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public ConnectToServerCtrl(ServerUtils server, ModalFactory modalFactory, MainCtrl mainCtrl) {
        this.server = server;
        this.modalFactory = modalFactory;
        this.mainCtrl = mainCtrl;
        this.animation = new AnimationUtils();
    }

    /**
     * Initializes the back button
     */
    public void initialize() {
        backBtn.setImage(new Image("/client/img/back_btn.png"));
        backButtonHandler();
        connectHandler();
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
            Alert alert = modalFactory.getModal(Alert.AlertType.ERROR, "Error", "", e.getMessage());
            alert.showAndWait();
            return;
        }

        if(goToScene.equals(AdminCtrl.class.getName())) {

            fadeOutServer("adminEdit");

        } else if(goToScene.equals(LeaderboardCtrl.class.getName())) {

            fadeOutServer("leaderboard");

        } else {

            fadeOutServer("main");

        }

    }

    /**
     * when clicking back button the user is redirected to the main page
     */
    public void goBackButton(){
        fadeOutServer("main");
    }

    /**
     * goes to the given scene and does fading animation
     * @param nextScene
     */
    public void fadeOutServer(String nextScene){
        animation.fadeOut(anchorPane, mainCtrl, nextScene);
    }

    /**
     * when entering the scene it does fading animation
     */
    public void fadeInServer(){
        animation.fadeIn(anchorPane);
    }

    /**
     *  The back button functionality
     */
    private void backButtonHandler() {
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> goBackButton());
        backBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            backBtn.setEffect(hover);
            backBtn.getStyleClass().add("hover-cursor");
        });
        backBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            backBtn.setEffect(null);
            backBtn.getStyleClass().remove("hover-cursor");
        });
    }

    /**
     * connect Button hover effects
     */
    private void connectHandler() {
        connect.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            connect.getStyleClass().add("hover-cursor");
            connect.getStyleClass().add("hover-buttonDark");
        });
        connect.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            connect.getStyleClass().remove("hover-cursor");
            connect.getStyleClass().remove("hover-buttonDark");
        });
    }

}
