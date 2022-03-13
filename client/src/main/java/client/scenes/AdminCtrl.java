package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
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

    /**
     * Constructor
     * @param server
     * @param mainCtrl
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
    }

    /**
     * Return to the main overview
     */
    public void cancel() {
        mainCtrl.showOverview();
    }

    /**
     * Gets a list of all activities and creates a table with these values
     */
    public void refresh() {
        var activities = server.getActivities();
        data = FXCollections.observableList(activities);
        table.setItems(data);
    }
}
