/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Quote;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;

public class QuoteOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObservableList<Quote> data;

    @FXML
    private TableView<Quote> table;
    @FXML
    private TableColumn<Quote, String> colFirstName;
    @FXML
    private TableColumn<Quote, String> colLastName;
    @FXML
    private TableColumn<Quote, String> colQuote;

    @FXML
    private ImageView imageDisplay;

    @FXML
    private TextField questionDisplayField;

    /**
     * Creates a QuoteOverviewCtrl, which controlles the display/interaction of the (main) overview screen.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * TODO: rename the class, remove attributes/methods concerning quotes
     */
    @Inject
    public QuoteOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * TODO: to remove
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colFirstName.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().person.firstName));
        colLastName.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().person.lastName));
        colQuote.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().quote));
    }

    /**
     * TODO: to remove
     */
    public void addQuote() {
        mainCtrl.showAdd();
    }

    /**
     * TODO: to remove
     */
    public void refresh() {
        var quotes = server.getQuotes();
        data = FXCollections.observableList(quotes);
        table.setItems(data);
    }

    /**
     * Displays the image for the first activity
     */
    public void onGetImage1ButtonClick() {

        Image img = new Image(ServerUtils.getImageURL("00/shower.jpg"));
        imageDisplay.setImage(img);

    }

    /**
     * Displays the image for the second activity
     */
    public void onGetImage2ButtonClick() {

        Image img = new Image(ServerUtils.getImageURL("00/smartphone.png"));
        imageDisplay.setImage(img);

    }

    /**
     * Retrieves a random question from the server using the server utilities and displays it
     */
    public void onGetQuestionButtonClick() {

        questionDisplayField.setText(server.getRandomQuestion().displayQuestion());

    }

}