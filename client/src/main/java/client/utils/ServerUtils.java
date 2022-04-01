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

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.List;

import commons.*;
import commons.gameupdate.GameUpdate;

import jakarta.ws.rs.core.Form;
import org.glassfish.jersey.client.ClientConfig;
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

    private static String SERVER = "";
    private static String WS_SERVER = "";
    private StompSession session;
    private UUID gameUUID;
    private boolean isInGame = false;

    /**
     * Tests the current server address to see if a connection can be established, and if
     * it is indeed a Quizzz Server
     * @return true if the current server could be connected to and it is a Quizzz Server, false
     * otherwise
     */
    public boolean connectionTest() {

        try {
            return ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER).path("")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(String.class)
                    .equals("Quizzz Server");
        } catch(Exception e) {
            return false;
        }

    }

    /**
     * Attempts to establish a WebSocket connection with the server at the specified URL
     * @param url the URL with which a WebSocket connection is to be established
     * @return a WebSocket session if it can be established
     */
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

    /**
     * Disconnects from the WebSocket session
     */
    public void disconnect() {

        if(session != null) {
            if(session.isConnected()) {
                session.disconnect();
            }
        }

    }

    /**
     * Subscribes to the gameupdates WebSocket topic for the game with the specified UUID. All
     * messages published to the topic will be sent to the specified consumer.
     * @param gameUUID the UUID of the game whose topic to subscribe to
     * @param consumer the consumer to send all the messages published to the topic to
     */
    public void registerForGameUpdates(UUID gameUUID, Consumer<GameUpdate> consumer) {

        registerForWebsocketMessages("/topic/gameupdates/" + gameUUID.toString(), GameUpdate.class, consumer);

    }

    /**
     * Sends game update to the game's websocket informing that emoji was sent
     * @param gameUpdate update containing sent emoji and username
     */
    public void sendEmoji(GameUpdate gameUpdate){
        session.send("/game/emoji/"+gameUUID.toString(),gameUpdate);
    }

    /**
     * Utility method to subscribe to a WebSocket topic
     * @param destination the URL (relative to the connected server) of the WebSocket topic
     * @param type the class of the message expected to be received from the topic
     * @param consumer the consumer to send all messages published to the topic to
     * @param <T> the type of the message expected to be received from the topic
     */
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
     * Registers for the game loop updates with the current stored game UUID, and sends
     * all incoming game loop updates to the provided consumer. The long poll loop is automatically
     * cancelled upon leaving the game by clicking the back button or closing the window, and it is guaranteed
     * by this method that no further updates will be accepted by the provided consumer after leaving the game.
     * @param consumer the consumer that accepts incoming game loop updates
     */
    public void registerForGameLoop(Consumer<GameUpdate> consumer, String username) {

        LongPollThread longPollThread = new LongPollThread(SERVER, gameUUID, consumer, username,isInGame);
        longPollThread.start();

    }

    /**
     * Gets the questions for a specific game using the API endpoint (sends a get request)
     * @return Returns the retrieved questions from the server
     */
    public List<Question> getQuestions() {

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/questions")
                .queryParam( "gameID", gameUUID.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});

    }

    /**
     * Sends the answer to a question to the server
     * @param answer the answer to send to the server
     * @param playerName the username of the player
     */
    public void sendAnswerToServer(long answer, String playerName) {

        Form postVariables = new Form();
        postVariables.param("gameID", gameUUID.toString());
        postVariables.param("playerName", playerName);
        postVariables.param("answer", String.valueOf(answer));

        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/answer")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(postVariables, APPLICATION_FORM_URLENCODED_TYPE), String.class);

    }


    /**
     * Informs the server that a time joker has been used, using the API endpoint
     * @param username the player that used the time joker
     * @param gameUUID UUID of the current game
     * @return The username of the player if the request was successful
     */
    public String useTimeJoker(String username, UUID gameUUID) {
        Form form = new Form();
        form.param("username", username);
        form.param("gameUUID", gameUUID.toString());

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("/api/jokers/time") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE), String.class);
    }

    /**
     * Informs the server that the question joker has been used, using the API endpoint
     * @param username the player that used the question joker
     * @param gameUUID UUID of the current game
     * @return The username of the player if the request was successful
     */
    public String useQuestionJoker(String username, UUID gameUUID) {
        Form form = new Form();
        form.param("username", username);
        form.param("gameUUID", gameUUID.toString());

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("/api/jokers/question") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE), String.class);
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
     * Sends a post request to import a list of activities
     * @param path the path to the json file
     * @return string indicating whether the request was successful
     */
    public String importActivity(String path) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("debug/activities/import") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(path, APPLICATION_JSON), String.class);
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

        String[] split = imagePath.split("/");
        return SERVER + "api/img/"  + URLEncoder.encode(split[0], StandardCharsets.UTF_8) + "/"
                                    + URLEncoder.encode(split[1], StandardCharsets.UTF_8);

    }

    /**
     * Sends a request to join a multiplayer game
     * @param username the requested username
     * @return a GameUpdateFullPlayerList if the player has joined a multiplayer game, or a GameUpdateNameInUse
     * if a player with the requested username is already in the current game
     */
    public GameUpdate joinMultiplayerGame(String username) {
        Form form = new Form();
        form.param("username", username);
        form.param("gametype", GameType.MULTIPLAYER.name());

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/user/enter") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE), GameUpdate.class);
    }

    /**
     * Sends a request to start a singleplayer game
     * @param username the requested username
     * @param confirmNameInUse true if the client wishes to start a singleplayer game even if the username
     *                         has been registered to the leaderboard before
     * @return a GameUpdateGameStarting if the game is starting, or a GameUpdateNameInUse if the name requested
     * has been registered to the leaderboard before and confirmNameInUse was set to false
     */
    public GameUpdate joinSinglePlayerGame(String username, boolean confirmNameInUse) {

        Form form = new Form();
        form.param("username", username);
        form.param("gametype", GameType.SINGLEPLAYER.name());
        form.param("confirmNameInUse", String.valueOf(confirmNameInUse));

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/user/enter") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE), GameUpdate.class);

    }

    /**
     * Informs the server that the specified player is leaving the game
     * @param username the username of the player that is leaving
     * @param gameUUID the UUID of the game that the player is leaving
     * @return the response from the server
     */
    public String leaveGame(String username, UUID gameUUID) {
        
        isInGame = false;

        disconnect();

        if(username == null || gameUUID == null) return "";

        Form form = new Form();
        form.param("username", username);
        form.param("gameUUID", gameUUID.toString());

        try {
            return ClientBuilder.newClient(new ClientConfig()) //
                    .target(SERVER).path("api/user/leave") //
                    .request(APPLICATION_JSON) //
                    .accept(APPLICATION_JSON) //
                    .post(Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE), String.class);
        } catch(Exception e) {
            return "";
        }

    }

    /**
     * Informs the server that a player has requested that the current game be started
     */
    public void startGame() {

        session.send("/game/start", "A game start has been requested.");

    }

    /**
     * Sends a post request for the server address
     * @param server - the address of the server
     */
    public void changeServer(String server) {

        if(server.contains("://")) {

            String[] split = server.split("://");
            if(split.length != 2) {
                throw new IllegalArgumentException("Malformed URL \"" + server + "\"");
            }

            server = split[1];

        }

        if(!server.endsWith("/"))
            server +="/";

        SERVER = "http://" + server;
        try {
            URL url = new URL(SERVER);
        } catch(MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL \"" + server + "\" - " + e.getMessage());
        }
        if(!connectionTest()) {
            throw new IllegalArgumentException("\"" + server +  "\" - Server not found or was not a Quizzz Server.");
        }
        WS_SERVER = "ws://" + server + "websocket";
        try {
            session = connect(WS_SERVER);
        } catch(Exception e) {
            throw new IllegalArgumentException("\"" + server +  "\" - Found a Quizzz Server at the specified URL, but could not connect its WebSocket topic.");
        }
    }

    /**
     * Sets the UUID of the game the client is in
     * @param gameUUID the UUID of the corresponding game
     */
    public void setGameUUID(UUID gameUUID) {
        this.gameUUID = gameUUID;
    }

    /**
     * Stores the fact that the client is in a game in this class by setting a boolean to true.
     * This allows the long polling begin and to be cancelled upon clicking the back button by
     * setting this variable to false again.
     */
    public void setInGameTrue() {

        isInGame = true;

    }

    /**
     * Gets a list of scores (username and points) registered to the server's leaderboard, guaranteed to be sorted by leaderboard rank ascending
     * @return all scores on the leaderboard sorted by rank ascending
     */
    public List<Score> getLeaderboard() {

        return ClientBuilder.newClient(new ClientConfig()) //
                                    .target(SERVER).path("api/scores/sorted") //
                                    .request(APPLICATION_JSON) //
                                    .accept(APPLICATION_JSON) //
                                    .get(new GenericType<List<Score>>() {});

    }

    /**
     *
     * @return true if the game is still going
     */
    public boolean getIsInTheGame(){
        return isInGame;
    }

}