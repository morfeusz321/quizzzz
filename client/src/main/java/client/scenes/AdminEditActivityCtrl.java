package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AdminEditActivityCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField id;
    @FXML
    private TextField imagePath;
    @FXML
    private TextField title;
    @FXML
    private TextField consumption;
    @FXML
    private Button create;
    @FXML
    private Button save;

    private Activity activity;

    /**
     * Creates a AdminEditActivityCtrl, which controls the display/interaction of editing the activities
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public AdminEditActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Sets the TextField values to the previously selected activity (or empty if creating a new activity)
     * @param activity selected activity
     */
    public void setActivity(Activity activity) {
        if(activity == null) {
            this.activity = null;
            save.setDisable(true);
            create.setDisable(false);
            id.setEditable(true);
        }
        else {
            this.activity = activity;
            id.setText(activity.id);
            imagePath.setText(activity.imagePath);
            title.setText(activity.title);
            consumption.setText(String.valueOf(activity.consumption));
            save.setDisable(false);
            create.setDisable(true);
            id.setEditable(false);
        }
    }

    /**
     * Return to the admin scene
     */
    public void cancel() {
        mainCtrl.showAdmin();
    }

    /**
     * Saves the modified activity to the database using an API endpoint
     */
    public void save() {
        try {
            this.activity.imagePath = imagePath.getText();
            this.activity.title = title.getText();
            this.activity.consumption = Long.parseLong(consumption.getText());
            server.editActivity(activity);
            mainCtrl.showAdmin();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid input");
            alert.setContentText("Consumption must be a number");
            alert.showAndWait();
        }
    }

    /**
     * Creates and saves a new activity to the database using an API endpoint
     */
    public void create() {
        try {
            String id = this.id.getText();
            String imagePath = this.imagePath.getText();
            String title = this.title.getText();
            long consumption = Long.parseLong(this.consumption.getText());
            activity = new Activity(id, imagePath, title, consumption);
            server.editActivity(activity);
            mainCtrl.showAdmin();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid input");
            alert.setContentText("Consumption must be a number");
            alert.showAndWait();
        }
    }
}
