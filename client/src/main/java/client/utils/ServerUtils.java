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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import commons.Activity;
import commons.AnswerResponseEntity;
import commons.Question;
import jakarta.ws.rs.core.Form;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

public class ServerUtils {

    private static String SERVER = "http://localhost:8080/";

    /**
     * Gets a random question from the server using the API endpoint (sends a get request)
     * @return Returns the retrieved question from the server
     */
    public Question getRandomQuestion() {

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/questions/random")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Question.class);

    }

    /**
     * Sends the answer to a question to the server
     * @param question the question to answer
     * @param answer the answer to send to the server
     * @return An AnswerResponseEntity which contains information about whether the answer was correct,
     * as well as the proximity to the correct answer for estimation questions
     */
    public AnswerResponseEntity sendAnswerToServer(Question question, long answer) {

        Form postVariables = new Form();
        postVariables.param("questionID", question.questionId.toString());
        postVariables.param("answer", String.valueOf(answer));

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/questions/answer")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(postVariables, APPLICATION_FORM_URLENCODED_TYPE), AnswerResponseEntity.class);

    }

    /**
     * Gets all activities from the server using the API endpoint
     * @return a list of activities
     */
    public List<Activity> getActivities() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("debug/activities") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Activity>>() {});
    }

    /**
     * Post a modified activity to the server using the API endpoint
     * @param activity modified activity
     * @return the new activity if the request was successful
     */
    public Activity editActivity(Activity activity) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("debug/activities/edit") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }

    /**
     * Sends a post request to delete an activity
     * @param activity the activity that will be deleted
     * @return the old activity (now deleted) if the request was successful
     */
    public Activity deleteActivity(Activity activity) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("debug/activities/delete") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }

    /**
     * Gets the URL where the server is located as a string (e.g. http://localhost:8080/)
     * @return Returns the URL where the server is located as a string
     */
    public static String getServer() {
        return SERVER;
    }

    /**
     * Gets the URL to a given image path
     * @param imagePath The path to the image which should be retrieved
     * @return Returns the URL to the given image path
     */
    public static String getImageURL(String imagePath) {

        return SERVER + "api/img/" + imagePath;

    }

    /**
     * Sends a post request for the player username
     * @param username - the name of the player
     * @return string indicating whether the request was successful
     */
    public String addUserName(String username) {
        Form postUsername = new Form();
        postUsername.param("username", username);

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/user/enter") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(postUsername, APPLICATION_FORM_URLENCODED_TYPE), String.class);
    }

    /**
     * Sends a post request for the server address
     * @param server - the address of the server
     */
    public void changeServer(String server) {
        if(!server.startsWith("http://"))
            server = "http://" + server;

        if(!server.endsWith("/"))
            server +="/";

        SERVER = server;
    }
}