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

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.GeneralQuestion;
import commons.Question;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private final ServerUtils server;
    private Stage primaryStage;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private GeneralQuestionCtrl generalQuestionCtrl;
    private Scene generalQuestion;

    private ComparisonQuestionCtrl comparisonQuestionCtrl;
    private Scene comparisonQuestion;

    private EstimationQuestionCtrl estimationQuestionCtrl;
    private Scene estimationQuestion;

    /**
     * Creates a MainCtrl, which controls displaying and switching between screens.
     * @param server Utilities for communicating with the server (API endpoint)
     */
    @Inject
    public MainCtrl(ServerUtils server) {
        this.server = server;
    }

    /**
     * Initialize the main control with the different scenes and controllers of each scene. This class
     * manages the switching between the scenes.
     * @param primaryStage The stage (i.e. window) for all scenes
     * @param overview Pair of the control and the scene of the overview
     * @param add Pair of the control and the scene for adding quotes TODO: to remove
     * @param generalQ Pair of the control and the scene of the general question
     * @param comparisonQ Pair of the control and the scene of the comparison question
     * @param estimationQ Pair of the control and the scene of the estimation question
     */
    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<GeneralQuestionCtrl, Parent> generalQ,
                           Pair<ComparisonQuestionCtrl, Parent> comparisonQ,
                           Pair<EstimationQuestionCtrl, Parent> estimationQ) {
        this.primaryStage = primaryStage;

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.generalQuestionCtrl = generalQ.getKey();
        this.generalQuestion = new Scene(generalQ.getValue());
        this.generalQuestion.getStylesheets().add(
                GeneralQuestionCtrl.class.getResource(
                        "/client/stylesheets/general-question-style.css"
                ).toExternalForm());
        this.generalQuestion.getStylesheets().add(
                GeneralQuestionCtrl.class.getResource(
                        "/client/stylesheets/screen-style.css"
                ).toExternalForm());

        this.comparisonQuestionCtrl = comparisonQ.getKey();
        this.comparisonQuestion = new Scene(comparisonQ.getValue());
        this.comparisonQuestion.getStylesheets().add(
                GeneralQuestionCtrl.class.getResource(
                        "/client/stylesheets/general-question-style.css"
                ).toExternalForm());
        this.comparisonQuestion.getStylesheets().add(
                GeneralQuestionCtrl.class.getResource(
                        "/client/stylesheets/screen-style.css"
                ).toExternalForm());

        this.estimationQuestionCtrl = estimationQ.getKey();
        this.estimationQuestion = new Scene(estimationQ.getValue());
        this.estimationQuestion.getStylesheets().add(
                GeneralQuestionCtrl.class.getResource(
                        "/client/stylesheets/general-question-style.css"
                ).toExternalForm());
        this.estimationQuestion.getStylesheets().add(
                GeneralQuestionCtrl.class.getResource(
                        "/client/stylesheets/screen-style.css"
                ).toExternalForm());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        nextQuestion(); // now starts with first question screen
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
     * Shows the general question screen and loads a new question
     */
    public void showGeneralQuestion(Question q) {
        primaryStage.setTitle("General question");
        primaryStage.setScene(generalQuestion);
        generalQuestionCtrl.loadQuestion(q);
        // TODO: display same question synchronously to all clients (this will probably be complicated)
    }

    /**
     * Shows the comparison question screen and loads a new question
     */
    public void showComparisonQuestion(Question q) {
        primaryStage.setTitle("Comparison question");
        primaryStage.setScene(comparisonQuestion);
        comparisonQuestionCtrl.loadQuestion(q);
        // TODO: display same question synchronously to all clients (this will probably be complicated)
    }

    /**
     * Shows the estimation question screen and loads a new question
     */
    public void showEstimationQuestion(Question q) {
        primaryStage.setTitle("Estimation question");
        primaryStage.setScene(estimationQuestion);
        estimationQuestionCtrl.loadQuestion(q);
        // TODO: display same question synchronously to all clients (this will probably be complicated)
    }

    /**
     * TODO: to remove
     */
    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    /**
     * Shows next question, the question type is selected randomly
     */
    public void nextQuestion() {
        Question q = server.getRandomQuestion();
        if(q instanceof GeneralQuestion) {
            showGeneralQuestion(q);
        }
        // TODO: other questions are not implemented yet, this has to be modified after that
    }
}