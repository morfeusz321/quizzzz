package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private Activity activity;

    /**
     * TODO: to remove
     * @param server TODO: to remove
     * @param mainCtrl TODO: to remove
     */
    @Inject
    public AdminEditActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Sets the TextField values to the previously selected activity
     * @param activity selected activity
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
        id.setText(activity.id);
        imagePath.setText(activity.imagePath);
        title.setText(activity.title);
        consumption.setText(String.valueOf(activity.consumption));
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
}
