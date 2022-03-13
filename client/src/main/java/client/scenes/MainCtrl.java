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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private OverviewCtrl overviewCtrl;
    private Scene overview;

    private UserCtrl userCtrl;
    private Scene username;

    /**
     * Initialize the main control with the different scenes and controllers of each scene. This class
     * manages the switching between the scenes.
     * @param primaryStage The stage (i.e. window) for all scenes
     * @param overview Pair of the control and the scene of the overview
     * @param username the name of the player
     */
    public void initialize(Stage primaryStage, Pair<OverviewCtrl, Parent> overview,
                           Pair<UserCtrl, Parent> username) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.userCtrl = username.getKey();
        this.username = new Scene(username.getValue());

        showOverview();
        primaryStage.show();
    }

    /**
     * Shows the overview scene (question display, image display)
     */
    public void showOverview() {
        primaryStage.setScene(overview);
    }

}