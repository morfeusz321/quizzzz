package server.game;

import commons.*;
import commons.gameupdate.*;
import org.apache.commons.lang3.builder.*;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Component
@Scope("prototype")
public class Game extends Thread {

    private static final long INITIAL_GAME_START_DELAY_MILLISECONDS = 1000L;
    private static final long QUESTION_TIME_MILLISECONDS = 15000L;
    private static final long TRANSITION_TIME_MILLISECONDS = 5000L;
    private static final long LEADERBOARD_TIME_MILLISECONDS = 10000L;

    private UUID uuid;
    private GameType gameType;

    private ConcurrentHashMap<String, Player> players;
    private List<Question> questions;
    private Question currentQuestion;
    private int currentQuestionIdx;
    private boolean done;

    private ConcurrentHashMap<String, DeferredResult<ResponseEntity<GameUpdate>>> deferredResultMap;

    private ConcurrentHashMap<String, Long> answerMap;

    private StopWatch stopWatch;
    private long lastTime;

    private ConcurrentHashMap<String, Score> leaderboard;

    @HashCodeExclude
    @EqualsExclude
    @ToStringExclude
    private GameUpdateManager gameUpdateManager;

    @HashCodeExclude
    @EqualsExclude
    @ToStringExclude
    private QuestionGenerator questionGenerator;

    /**
     * Creates a new game
     * @param gameUpdateManager the game update manager used by this game to send messages to the client
     * @param questionGenerator the question generator
     */
    public Game(GameUpdateManager gameUpdateManager, QuestionGenerator questionGenerator) {

        this.gameUpdateManager = gameUpdateManager;
        this.questionGenerator = questionGenerator;
        this.players = new ConcurrentHashMap<>();
        this.questions = new ArrayList<>(); // questions are "loaded" when game is started
        this.done = false;
        this.deferredResultMap = new ConcurrentHashMap<>();
        this.answerMap = new ConcurrentHashMap<>();

        this.stopWatch = new StopWatch();
        this.lastTime = 0;

        this.leaderboard = new ConcurrentHashMap<>();

    }

    /**
     * Starts the game and initializes the questions.
     */
    @Override
    public void run(){
        // Minimum amount of questions per question type is set to 2 here
        try {
            questions = questionGenerator.generateGameQuestions(2);
        } catch (IllegalArgumentException e) {
            // This will only be the case, if minPerQuestionType is not valid.
            return;
        }

        // Something went wrong when trying to generate the questions
        if(questions == null){
            // TODO: some error handling here, maybe send an error message to client that
            //  should then be displayed
            return;
        }

        // Set first question
        currentQuestionIdx = -1;

        gameUpdateManager.startGame(this.uuid);

        this.stopWatch.start();

        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                gameLoop();
            }
        }, Game.INITIAL_GAME_START_DELAY_MILLISECONDS);

    }

    /**
     * Updates the current question and informs all registered long polls of this update
     */
    private void gameLoop() {

        stopWatch.stop();

        if(currentQuestionIdx == 19) {

            currentQuestionIdx++;
            done = true;
            deferredResultMap.forEach((username, res) -> res.setResult(ResponseEntity.ok(
                    new GameUpdateGameFinished(
                            createLeaderboardList()
                    )
            )));
            deferredResultMap.clear();

            return;

        }

        lastTime = stopWatch.getTotalTimeMillis();
        stopWatch.start();

        currentQuestionIdx++;
        this.currentQuestion = questions.get(currentQuestionIdx);

        deferredResultMap.forEach((username, res) -> res.setResult(ResponseEntity.ok(new GameUpdateNextQuestion(currentQuestionIdx))));
        deferredResultMap.clear();

        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                sendTransitionPeriod();
            }
        }, Game.QUESTION_TIME_MILLISECONDS);

    }

    /**
     *  sets the answer in the ConcurrentHashMap
     * @param username the username of the Player
     * @param answer the answer the user chose for the question
     */

    public void saveAnswer(String username, long answer) {
        this.answerMap.put(username, answer);
    }


    /**
     * Informs all registered long poll requests that the current game is entering the transition period
     */
    private void sendTransitionPeriod() {

        for(Map.Entry<String, DeferredResult<ResponseEntity<GameUpdate>>> openRequest : deferredResultMap.entrySet()) {

            String username = openRequest.getKey();
            DeferredResult<ResponseEntity<GameUpdate>> req = openRequest.getValue();
            deferredResultMap.remove(username);

            long answer;
            if(!answerMap.containsKey(username)) {
                answer = -1;
            } else {
                answer = answerMap.get(username);
            }

            req.setResult(ResponseEntity.ok(new GameUpdateTransitionPeriodEntered(AnswerResponseEntity.generateAnswerResponseEntity(currentQuestion, answer))));

            // TODO: Save scores to leaderboard here, calculate points

        }

        answerMap.clear();

        if(currentQuestionIdx == 9) {
            (new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    sendLeaderboard();
                }
            }, Game.TRANSITION_TIME_MILLISECONDS);
        } else {
            (new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    gameLoop();
                }
            }, Game.TRANSITION_TIME_MILLISECONDS);
        }

    }

    /**
     * Informs all registered long polls that the intermediate leaderboard should be displayed
     */
    private void sendLeaderboard() {

        deferredResultMap.forEach((username, res) -> res.setResult(ResponseEntity.ok(new GameUpdateDisplayLeaderboard(createLeaderboardList()))));
        deferredResultMap.clear();

        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                gameLoop();
            }
        }, Game.LEADERBOARD_TIME_MILLISECONDS);

    }

    /**
     * Creates a list of Scores from the internal HashMap used to hold the scores by this game, sorted by scores descending
     * @return a list of scores for players in this game sorted by scores descending
     */
    private List<Score> createLeaderboardList() {

        List<Score> result = new ArrayList<>(leaderboard.values().stream().sorted(Comparator.comparingInt(s -> s.score)).toList());
        Collections.reverse(result);
        return result;

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
     * long poll whenever the game phase changes.
     * @param username the username of the player that this long poll request was sent by
     * @param deferredResult the long poll request to inform of updates
     */
    public void runDeferredResult(String username, DeferredResult<ResponseEntity<GameUpdate>> deferredResult) {

        this.deferredResultMap.put(username, deferredResult);

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
     * Returns the current question of this game
     * @return the current question of this game
     */
    private Question getCurrentQuestion() {
        return currentQuestion;
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
        if(obj == null){
            return false;
        }
        if(obj instanceof Game){
            Game other = (Game) obj;
            return this.uuid.equals(other.uuid) &&
            this.gameType.equals(other.gameType) &&
            this.players.equals(other.players) &&
            (done == other.done) &&
            this.questions.equals(other.questions) &&
            Objects.equals(this.currentQuestion, other.currentQuestion);
        }
        return false;
    }



    /**
     * Generate a hash code for this object
     * @return hash code
     */
    @Override
    public int hashCode() {

        return uuid.hashCode() ^ gameType.hashCode() ^ players.hashCode() ^ Boolean.hashCode(done) ^ questions.hashCode();

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
