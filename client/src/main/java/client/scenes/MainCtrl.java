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

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private BasicQuestionCtrl basicQuestionCtrl;
    private Scene basicQuestion;

    /**
     * Initialize the main control with the different scenes and controllers of each scene. This class
     * manages the switching between the scenes.
     * @param primaryStage The stage (i.e. window) for all scenes
     * @param overview Pair of the control and the scene of the overview
     * @param add Pair of the control and the scene for adding quotes TODO: to remove
     */
    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
            Pair<AddQuoteCtrl, Parent> add, Pair<BasicQuestionCtrl, Parent> basicQuestion) {
        this.primaryStage = primaryStage;

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.basicQuestionCtrl = basicQuestion.getKey();
        this.basicQuestion = new Scene(basicQuestion.getValue());
        this.basicQuestion.getStylesheets().add(
                BasicQuestionCtrl.class.getResource(
                        "/client/stylesheets/basic-question-style.css"
                ).toExternalForm());
        this.basicQuestion.getStylesheets().add(
                BasicQuestionCtrl.class.getResource(
                        "/client/stylesheets/screen-style.css"
                ).toExternalForm());
        this.basicQuestionCtrl.initializeAnswerEventHandlers();
        this.basicQuestionCtrl.showImages();

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        showBasicQuestion(); // now starts with question screen
        primaryStage.show();
    }

    /**
     * Shows the overview scene (table for quotes, question display, image display)
     * TODO: remove table for quotes
     */
    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    /**
     * Shows the overview scene (table for quotes, question display, image display)
     */
    public void showBasicQuestion() {
        primaryStage.setTitle("Basic question");
        primaryStage.setScene(basicQuestion);
    }

    /**
     * TODO: to remove
     */
    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }
}