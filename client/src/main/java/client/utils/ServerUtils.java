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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import commons.Question;
import jakarta.ws.rs.core.Form;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
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
     * TODO: to remove
     * @throws IOException TODO: to remove
     */
    public void getQuotesTheHardWay() throws IOException {
        var url = new URL("http://localhost:8080/api/quotes");
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * TODO: to remove
     */
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
    }

    /**
     * TODO: to remove
     * @param quote TODO: to remove
     * @return TODO: to remove
     */
    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
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
     * @return string indicating whether the request was successful
     */
    public String changeServer(String server) {
        Form postServer = new Form();
        postServer.param("server", server);
        SERVER = server;

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/user/enter") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(postServer, APPLICATION_FORM_URLENCODED_TYPE), String.class);
    }
}