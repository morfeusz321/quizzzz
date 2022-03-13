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
package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.*;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * Launches the client application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Starts the application by loading the fxml files/scenes and by initializing the
     * main control with the overview/quote adding. TODO: remove quote adding
     * @param primaryStage The main application stage (i.e. window) in which all scenes are displayed
     */
    @Override
    public void start(Stage primaryStage){

        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var username = FXML.load(UserCtrl.class, "client", "scenes", "UserOverview.fxml");
        var admin = FXML.load(AdminCtrl.class, "client", "scenes", "AdminOverview.fxml");
        var adminEdit = FXML.load(AdminEditActivityCtrl.class, "client", "scenes", "AdminEditActivity.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, overview, add, username, admin,adminEdit);
    }
}