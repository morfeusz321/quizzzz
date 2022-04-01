package client.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ModalFactory {

    /**
     * Creates a modal with styling and the provided text elements
     * @param alertType the type of this alert
     * @param title the title for the alert
     * @param text the header text for the alert
     * @param body the content text for the alert
     * @return an alert with the provided text elements and styling applied
     */
    public static Alert getModal(Alert.AlertType alertType, String title, String text, String body) {

        Alert alert = new Alert(alertType);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(ModalFactory.class.getResource("/client/stylesheets/myDialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        String pathToLightbulb = ModalFactory.class.getResource("/client/img/main_lightbulb.png").toExternalForm();
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(
                new Image(pathToLightbulb));

        ImageView lightBulbIcon = new ImageView(pathToLightbulb);
        lightBulbIcon.setFitHeight(100);
        lightBulbIcon.setFitWidth(100);
        dialogPane.setGraphic(lightBulbIcon);

        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.setContentText(body);

        return alert;

    }

    /**
     * Creates a modal with styling and the provided text elements
     * @param alertType the type of this alert
     * @param title the title for the alert
     * @param text the header text for the alert
     * @return an alert with the provided text elements and styling applied
     */
    public static Alert getModal(Alert.AlertType alertType, String title, String text) {

        return getModal(alertType, title, text, "");

    }

}
