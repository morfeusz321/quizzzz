package client.scenes;

import client.utils.AnimationUtils;
import client.utils.ModalFactory;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminCtrl implements Initializable {

    private final ServerUtils server;
    private final ModalFactory modalFactory;
    private final MainCtrl mainCtrl;
    private final AnimationUtils animation;

    private ObservableList<Activity> data;

    @FXML
    private TableView<Activity> table;
    @FXML
    private TableColumn<Activity, String> id;
    @FXML
    private TableColumn<Activity, String> imagePath;
    @FXML
    private TableColumn<Activity, String> title;
    @FXML
    private TableColumn<Activity, String> consumption;
    @FXML
    private Label totalNo;
    @FXML
    private Label selected;
    @FXML
    private Button edit;
    @FXML
    private Button delete;
    @FXML
    private Button add;
    @FXML
    private Button imports;
    @FXML
    private Button importsUpdate;
    @FXML
    private ImageView backBtn;

    @FXML
    private AnchorPane anchorPane;

    private Activity currentActivity;
    private Scene adminScene;
    private List<Button> buttonList;

    /**
     * Constructor for a AdminCtrl, which controls the display/interaction of managing the activities
     *
     * @param server       Utilities for communicating with the server (API endpoint)
     * @param modalFactory the modal factory to use
     * @param mainCtrl     The main control which is used for calling methods to switch scenes
     */
    @Inject
    public AdminCtrl(ServerUtils server, ModalFactory modalFactory, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.modalFactory = modalFactory;
        this.animation = new AnimationUtils();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().id));
        imagePath.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().imagePath));
        title.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().title));
        consumption.setCellValueFactory(q -> new SimpleStringProperty(String.valueOf(q.getValue().consumption)));
        buttonList = List.of(edit, delete, imports, importsUpdate, add);
        initializeEventHandlers();

        backButtonHandler();
        backBtn.setImage(new Image("/client/img/back_btn.png"));
    }

    /**
     * The back button functionality
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
     * Return to the main overview
     */
    public void cancel() {
        mainCtrl.showMainScreen();
    }

    /**
     * Method for deleting an activity from the database
     */
    public void delete() {
        Alert alert = modalFactory.getModal(Alert.AlertType.CONFIRMATION, "Confirmation",
                "Are you sure you want to delete this entry?",
                "This will delete " + currentActivity.id);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            server.deleteActivity(currentActivity);
            refresh();
        } else {
            alert.close();
        }
    }

    /**
     * Sets the current scene to admin scene (required for file chooser)
     *
     * @param scene admin scene
     */
    public void setScene(Scene scene) {
        this.adminScene = scene;
    }

    /**
     * Opens a file chooser window where you can choose an activities.json file to import and override
     * (delete) the current DB
     */
    public void importActivity() {
        FileChooser fileChooser = new FileChooser();
        try {
            File selectedFile = fileChooser.showOpenDialog(adminScene.getWindow());
            String path = selectedFile.getPath();
            server.importActivity(path);
            refresh();
        } catch(NullPointerException e) {
            System.out.println("Selected file is null");
        }
    }

    /**
     * Opens a file chooser window where you can choose an activities.json file to import without
     * overriding the current DB
     */
    public void importActivityUpdate() {
        FileChooser fileChooser = new FileChooser();
        try {
            File selectedFile = fileChooser.showOpenDialog(adminScene.getWindow());
            String path = selectedFile.getPath();
            server.importActivityUpdate(path);
            refresh();
        } catch(NullPointerException e) {
            System.out.println("Selected file is null");
        }
    }

    /**
     * Show the edit activity scene (sending null as activity, in order to create a new activity)
     * reusing the same scene as edit activity
     */
    public void addActivity() {
        mainCtrl.showAdminEdit(null);
    }

    /**
     * Show the edit activity scene
     */
    public void showEdit() {
        mainCtrl.showAdminEdit(currentActivity);
    }

    /**
     * Gets a list of all activities and creates a table with these values
     */
    public void refresh() {
        table.getItems().clear();
        var activities = server.getActivities();
        data = FXCollections.observableList(activities);
        table.setItems(data);
        totalNo.setText("Total number of activities: " + table.getItems().size());
        edit.setDisable(true);
        delete.setDisable(true);
    }

    /**
     * Automatically updates currentActivity to the selected activity from the table
     *
     * @param click mouse event
     */
    @FXML
    private void handleClickTableView(MouseEvent click) {
        Activity activity = table.getSelectionModel().getSelectedItem();
        if(activity == null) {
            currentActivity = null;
            selected.setText("");
            return;
        }
        selected.setText("Currently selected: " + activity.id);
        currentActivity = activity;
        edit.setDisable(false);
        delete.setDisable(false);
    }

    /**
     * when clicking back button the user is redirected to the main page
     */
    public void goBackButton() {
        fadeOutAdmin("main");
    }

    /**
     * goes to the given scene and does fading animation
     *
     * @param nextScene
     */
    public void fadeOutAdmin(String nextScene) {
        animation.fadeOut(anchorPane, mainCtrl, nextScene);
    }

    /**
     * when entering the scene it does fading animation
     */
    public void fadeInAdmin() {
        animation.fadeIn(anchorPane);
    }

    /**
     * Gives a button its event handlers
     *
     * @param btn the button to give event handlers to
     */
    private void addEventHandlersToButton(Button btn) {

        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> eventHandlerButtonMouseEntered(btn));
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> eventHandlerButtonMouseExited(btn));

    }

    /**
     * The event handler called when the user hovers over a button
     *
     * @param btn the button that was hovered over
     */
    private void eventHandlerButtonMouseEntered(Button btn) {

        btn.getStyleClass().add("hover-buttonDark");
        btn.getStyleClass().add("hover-cursor");
    }

    /**
     * The event handler called when the user stops hovering over a button
     *
     * @param btn the button that was stopped hovering over
     */
    private void eventHandlerButtonMouseExited(Button btn) {

        btn.getStyleClass().remove("hover-buttonDark");
        btn.getStyleClass().remove("hover-cursor");

    }

    /**
     * Initializes the event handlers of all buttons
     */
    private void initializeEventHandlers() {

        buttonList.forEach(this::addEventHandlersToButton);

    }

}
