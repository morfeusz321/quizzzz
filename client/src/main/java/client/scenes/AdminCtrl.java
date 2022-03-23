package client.scenes;

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
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

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
    private ImageView backBtn;

    private Activity currentActivity;
    private Scene adminScene;

    /**
     * Constructor for a AdminCtrl, which controls the display/interaction of managing the activities
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public AdminCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().id));
        imagePath.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().imagePath));
        title.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().title));
        consumption.setCellValueFactory(q -> new SimpleStringProperty(String.valueOf(q.getValue().consumption)));
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> mainCtrl.showMainScreen());
        backBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> backBtn.setEffect(hover));
        backBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> backBtn.setEffect(null));
        backBtn.setImage(new Image("/client/img/back_btn.png"));
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to delete this entry?");
        alert.setContentText("This will delete " + currentActivity.id);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            server.deleteActivity(currentActivity);
            refresh();
        } else {
            alert.close();
        }
    }

    /**
     * Sets the current scene to admin scene (required for file chooser)
     * @param scene admin scene
     */
    public void setScene(Scene scene) {
        this.adminScene = scene;
    }

    /**
     * Opens a file chooser window where you can choose an activities.json file to import
     */
    public void importActivity() {
        FileChooser fileChooser = new FileChooser();
        try {
            File selectedFile = fileChooser.showOpenDialog(adminScene.getWindow());
            String path = selectedFile.getPath();
            server.importActivity(path);
            refresh();
        } catch (NullPointerException e) {
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
     * @param click mouse event
     */
    @FXML
    private void handleClickTableView(MouseEvent click) {
        Activity activity = table.getSelectionModel().getSelectedItem();
        selected.setText("Currently selected: " + activity.id);
        currentActivity = activity;
        edit.setDisable(false);
        delete.setDisable(false);
    }

}
