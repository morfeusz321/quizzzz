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
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import commons.AnswerResponseEntity;
import commons.Question;
import commons.gameupdate.GameUpdate;
import jakarta.ws.rs.core.Form;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";
    private static final String WS_SERVER = "ws://localhost:8080/websocket";
    private StompSession session = connect(WS_SERVER);

    private StompSession connect(String url) {

        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {}).get();
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch(ExecutionException e) {
            throw new RuntimeException(e);
        }

        throw new IllegalStateException();

    }

    public void registerForGameUpdates(UUID gameUUID, Consumer<GameUpdate> consumer) {

        registerForWebsocketMessages("/topic/gameupdates/" + gameUUID.toString(), GameUpdate.class, consumer);

    }

    private <T> void registerForWebsocketMessages(String destination, Class<T> type, Consumer<T> consumer) {

        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });

    }

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
     * Sends a post request
     * @param username - the name of the player
     * @return string indicating whether the request was successful
     */
    public GameUpdate addUserName(String username) {
        Form postUsername = new Form();
        postUsername.param("username", username);

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/user/enter") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(postUsername, APPLICATION_FORM_URLENCODED_TYPE), GameUpdate.class);
    }

    public String leaveGame(String username, UUID gameUUID) {
        Form form = new Form();
        form.param("username", username);
        form.param("gameUUID", gameUUID.toString());

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/user/leave") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE), String.class);
    }

    public void startGame() {

        session.send("/game/start", "A game start has been requested.");

    }

}