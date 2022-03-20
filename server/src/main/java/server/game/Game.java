package server.game;

import commons.GameType;
import commons.Player;
import commons.Question;
import org.apache.commons.lang3.builder.*;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.async.DeferredResult;
import server.api.QuestionController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Component
@Scope("prototype")
public class Game extends Thread {

    private UUID uuid;
    private GameType gameType;

    private ConcurrentHashMap<String, Player> players;
    private List<Question> questions;
    private Question currentQuestion;
    private int currentQuestionIdx;
    private boolean done;
    private int minPerQuestionType;

    private ConcurrentHashMap<UUID, DeferredResult<ResponseEntity<String>>> deferredResultMap;

    private StopWatch stopWatch;
    private long lastTime;

    @HashCodeExclude
    @EqualsExclude
    @ToStringExclude
    private GameUpdateManager gameUpdateManager;

    @HashCodeExclude
    @EqualsExclude
    @ToStringExclude
    private QuestionController questionController;

    /**
     * Creates a new game
     * @param gameUpdateManager the game update manager used by this game to send messages to the client
     * @param questionController the question generator (so that the server does not have to send API requests to itself)
     */
    public Game(GameUpdateManager gameUpdateManager, QuestionController questionController) {

        // TODO: not sure if the server should send requests for questions to itself via the API mapping (this does not
        //  really make sense, and would create overhead), so I included the QuestionController as a parameter for now.
        //  If this is not the best way of handling it, we would have to change this here.

        this.gameUpdateManager = gameUpdateManager;
        this.questionController = questionController;
        this.players = new ConcurrentHashMap<>();
        this.questions = new ArrayList<>(); // questions are "loaded" when game is started
        this.done = false;
        this.minPerQuestionType = 2; // minimum amount of questions per question type
        this.deferredResultMap = new ConcurrentHashMap<>();

        this.stopWatch = new StopWatch();
        this.lastTime = 0;

    }

    /**
     * Starts the game and initializes the questions.
     */
    @Override
    public void run(){

        // Generate the minimum amount of questions per question type
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < minPerQuestionType; j++){
                ResponseEntity<Question> generated = switch (i) {
                    case 0 -> questionController.getGeneralQuestion();
                    case 1 -> questionController.getComparisonQuestion();
                    case 2 -> questionController.getEstimationQuestion();
                    default -> questionController.getWhichIsMoreQuestion(); // is for case i = 3
                };
                if(!generated.getStatusCode().equals(HttpStatus.OK)){
                    // TODO: some error handling here, maybe send an error message to client that
                    //  should then be displayed
                    return;
                }
                questions.add(generated.getBody());
            }
        }

        // Generate the questions with random types
        for(int i = 0; i < 20 - (4 * minPerQuestionType); i++){
            ResponseEntity<Question> generated = questionController.getRandomQuestion();
            if(!generated.getStatusCode().equals(HttpStatus.OK)){
                // TODO: some error handling here, maybe send an error message to client that
                //  should then be displayed
                return;
            }
            questions.add(generated.getBody());
        }

        // The first questions are ordered per type, so shuffle the question list
        Collections.shuffle(questions);

        // Set first question
        currentQuestionIdx = -1;

        gameUpdateManager.startGame(this.uuid);

        // TODO: game loop here, after all questions, set done variable to true

        this.stopWatch.start();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(currentQuestionIdx == 19) {
                    currentQuestionIdx++;
                    done = true;
                    stopWatch.stop();
                    timer.cancel();
                } else {
                    gameLoop();
                }
            }
        }, 1000, 15000);

    }

    /**
     * Updates the current question and informs all registered long polls of this update
     */
    private void gameLoop() {

        stopWatch.stop();
        lastTime = stopWatch.getTotalTimeMillis();
        stopWatch.start();

        currentQuestionIdx++;
        this.currentQuestion = questions.get(currentQuestionIdx);

        deferredResultMap.forEach((uuid, res) -> res.setResult(ResponseEntity.ok(String.valueOf(currentQuestionIdx))));
        deferredResultMap.clear();

    }

    /**
     * Returns the amount of time that the current question has already been the current question
     * @return the elapsed time in this round of the game
     */
    public long getElapsedTimeThisQuestion() {

        if(!stopWatch.isRunning()) return 0L;

        stopWatch.stop();
        long ret = stopWatch.getTotalTimeMillis() - lastTime;
        stopWatch.start();

        return ret;

    }

    /**
     * Allows a long poll to be registered to this game. This game will update this
     * long poll whenever the current question updates.
     * @param deferredResult the long poll to inform of updates
     */
    public void runDeferredResult(DeferredResult<ResponseEntity<String>> deferredResult) {

        if(currentQuestionIdx >= 19) {
            deferredResult.setResult(ResponseEntity.ok("20"));
        } else {
            this.deferredResultMap.put(UUID.randomUUID(), deferredResult);
        }

    }

    /**
     * Gets the list of all questions
     * @return the list of all questions
     */
    public List<Question> getQuestions(){
        return questions;
    }

    /**
     * Returns whether this game is done, i.e. all 20 questions have been answered/displayed
     * @return whether this game is done
     */
    public boolean isDone(){
        return done;
    }

    /**
     * Sets this game's UUID
     * @param uuid the UUID for this game
     */
    public void setUUID(UUID uuid) {

        this.uuid = uuid;

    }

    /**
     * Returns this game's UUID
     * @return this game's UUID
     */
    public UUID getUUID() {

        return this.uuid;

    }

    /**
     * Sets this game's game type
     * @param gameType the game type for this game
     */
    public void setGameType(GameType gameType) {

        this.gameType = gameType;

    }

    /**
     * Returns this game's game type
     * @return this game's game type
     */
    public GameType getGameType() {

        return this.gameType;

    }

    /**
     * Returns a list of all players in this game
     * @return all players in this game
     */
    public List<Player> getPlayers() {

        List<Player> list = new ArrayList<>();
        list.addAll(players.values());

        return list;

    }

    /**
     * Returns a player in this game with the specified username
     * @param username the username of the player to retrieve
     * @return the player with that username if it can be found, or null if it can't
     */
    public Player getPlayer(String username) {

        return this.players.getOrDefault(username, null);

    }

    /**
     * Adds a player to this game. Note that the Game assumes that it has been confirmed already that a
     * player with this username is not yet in the Game, and that this is *not* checked again
     * in this method
     * @param player the player that is joining
     */
    protected void addPlayer(Player player) {

        this.players.put(player.getUsername(), player);

    }

    /**
     * Removes a player from this game
     * @param player the player that is leaving
     */
    public void removePlayer(Player player) {

        if(player == null) return;

        this.players.remove(player.getUsername());

        // It is not checked here, whether a game has 0 players. This is checked in the GameController,
        // because in that case this Game has to be removed, this cannot be done from here.

    }

    /**
     * Removes a player from this game by username
     * @param username the username of the player that is leaving
     */
    public void removePlayer(String username) {

        this.players.remove(username);

    }

    /**
     * Checks whether the specified player is in this game
     * @param player the player to check
     * @return true if the player is in this game, false otherwise
     */
    public boolean containsPlayer(Player player) {

        return this.players.containsKey(player.getUsername());

    }

    /**
     * Checks whether the specified player is in this game by its username
     * @param username the username of the player to check
     * @return true if the player with the specified username is in this game, false otherwise
     */
    public boolean containsPlayer(String username) {

        return this.players.containsKey(username);

    }

    /**
     * Checks if 2 game objects are equal
     * @param obj the object that will be compared
     * @return true or false, whether the objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generate a hash code for this object
     * @return hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Creates a formatted string for this object
     * @return a formatted string in multi line style
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
