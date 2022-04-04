package server.game;

import commons.*;
import commons.gameupdate.*;
import org.apache.commons.lang3.builder.*;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.async.DeferredResult;
import server.api.ScoreController;
import server.game.questions.QuestionGenerator;

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

    private ConcurrentHashMap<String, AnswerResponseEntity> answerMap;

    private StopWatch stopWatch;
    private long lastTime;

    private ScoreController scoreController;
    private ConcurrentHashMap<String, Score> leaderboard;

    private ConcurrentHashMap<String, Long> timeJoker;
    private ConcurrentHashMap<String, Boolean> scoreJoker;

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
        this.timeJoker = new ConcurrentHashMap<>();
        this.scoreJoker = new ConcurrentHashMap<>();

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
        initializeTimeJoker();
        initializeScoreJoker();
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

            List<Score> list;
            if(gameType==GameType.SINGLEPLAYER){
                list = sendDatabase();
            }
            else if(gameType==GameType.MULTIPLAYER){
                list = createLeaderboardList();
            }
            else{
                list = new ArrayList<>();
            }

            deferredResultMap.forEach((username, res) -> res.setResult(ResponseEntity.ok(
                    new GameUpdateGameFinished(
                            list
                    )
            )));
            deferredResultMap.clear();

            return;

        }

        initializeTimeJoker();

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
        long timeClicked = getElapsedTimeThisQuestion();
        long remainingTime = QUESTION_TIME_MILLISECONDS - getElapsedTimeThisQuestion();
        long oldTime = 0L;
        for(Map.Entry<String, Long> player : timeJoker.entrySet()) {
            if(oldTime<player.getValue()) {
                oldTime = player.getValue();
            }
        }
        long elapsedTime = oldTime - remainingTime;
        for(Map.Entry<String, Long> player : timeJoker.entrySet()) {
            player.setValue(player.getValue()-elapsedTime);
        }
        if(timeJoker.get(username) >= 0) {
            this.answerMap.put(username, AnswerResponseEntity.generateAnswerResponseEntity(currentQuestion, answer, (int) timeClicked));
        }
    }


    /**
     * Informs all registered long poll requests that the current game is entering the transition period
     */
    private void sendTransitionPeriod() {

        for(Map.Entry<String, DeferredResult<ResponseEntity<GameUpdate>>> openRequest : deferredResultMap.entrySet()) {

            String username = openRequest.getKey();
            DeferredResult<ResponseEntity<GameUpdate>> req = openRequest.getValue();
            deferredResultMap.remove(username);

            AnswerResponseEntity answer;
            answer = answerMap.getOrDefault(username, AnswerResponseEntity.generateAnswerResponseEntity(currentQuestion, -1, 0));

            if(scoreJoker.containsKey(username) && scoreJoker.get(username)) {
                answer.doublePoints();
                scoreJoker.remove(username);
            }

            req.setResult(ResponseEntity.ok(new GameUpdateTransitionPeriodEntered(answer)));

            // TODO: Save scores to leaderboard here, calculate points
            saveScoreToLeaderboard(answer.getPoints(), username);

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
     * adds the new score to the leaderboard to the given username
     * @param score amount of points received
     * @param username name of the player
     */
    public void saveScoreToLeaderboard(int score, String username){
        if(!leaderboard.containsKey(username)){
            leaderboard.put(username, new Score(username, score));
        }
        else{
            int currentScore = leaderboard.get(username).getScore();
            leaderboard.put(username, new Score(username, score+currentScore));
        }
    }

    /**
     * Informs all registered long polls that the intermediate leaderboard should be displayed
     */
    private void sendLeaderboard() {
        List<Score> listOfScores;
        if(gameType == GameType.MULTIPLAYER){
            listOfScores = createLeaderboardList();
        }
        // if the game is singleplayer the leaderboard is not needed
        else {
            listOfScores = sendDatabase();
        }
        deferredResultMap.forEach((username, res) -> res.setResult(ResponseEntity.ok(new GameUpdateDisplayLeaderboard(listOfScores))));
        deferredResultMap.clear();

        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                gameLoop();
            }
        }, Game.LEADERBOARD_TIME_MILLISECONDS);

    }

    /**
     * send the score database from the server
     * @return database list sorted
     */
    public List<Score> sendDatabase(){
        saveScores();
        return scoreController.getAllSorted();
    }

    /**
     * when the leaderboard is supposed to be shown the scores from that game have to be stored to the database
     */
    private void saveScores(){
        ConcurrentHashMap<String, Score> leaderboard = getLeaderboard();
        List<Player> players = getPlayers();
        for(Player p: players){
            String username = p.getUsername();
            Score score = leaderboard.get(username);
            scoreController.addScore(username, score.getScore());
        }
    }

    /**
     * Creates a list of Scores from the internal HashMap used to hold the scores by this game, sorted by scores descending
     * @return a list of scores for players in this game sorted by scores descending
     */
    public List<Score> createLeaderboardList() {

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
     * Informs all registered long polls that a time joker has been used
     * @param username the username of the player that initiated the time joker
     */
    public void useTimeJoker(String username) {

        long remainingTime = QUESTION_TIME_MILLISECONDS - getElapsedTimeThisQuestion();

        if(timeJoker.get(username) == 15000L) {
            long newTime = (long) (0.65 * remainingTime);
            for(Map.Entry<String, Long> player : timeJoker.entrySet()) {
                if(!player.getKey().equals(username)) {
                    player.setValue(newTime);
                }
                else player.setValue(remainingTime);
            }
        }
        else {

            long oldTime = 0L;
            for(Map.Entry<String, Long> player : timeJoker.entrySet()) {
                if(oldTime<player.getValue()) {
                    oldTime = player.getValue();
                }
            }
            long elapsedTime = oldTime - remainingTime;
            for(Map.Entry<String, Long> player : timeJoker.entrySet()) {
                player.setValue(player.getValue()-elapsedTime);
                long newTime = (long) (0.65 * player.getValue());
                if(!player.getKey().equals(username)) player.setValue(newTime);
            }
        }

        deferredResultMap.forEach((user, res) -> res.setResult(ResponseEntity.ok(new GameUpdateTimerJoker(timeJoker))));
        deferredResultMap.clear();

    }

    /**
     * Server-side handling of the question joker, returns an id of a button that will be removed
     * @param username the name of the player that initiated the question joker
     */
    public void useQuestionJoker(String username) {

        Question question = getCurrentQuestion();
        long answer = question.answer;
        Random random = new Random();
        int returnValue = switch ((int) answer) {
            case 1 -> random.nextBoolean() ? 2 : 3;
            case 2 -> random.nextBoolean() ? 1 : 3;
            case 3 -> random.nextBoolean() ? 1 : 2;
            default -> 0;
        };
        deferredResultMap.get(username).setResult(ResponseEntity.ok(new GameUpdateQuestionJoker(returnValue)));

    }

    /**
     * Server-side handling of the double points joker, maps true to the username in scoreJoker
     * @param username the name of the player that initiated the double points joker
     */
    public void useScoreJoker(String username) {
        if(scoreJoker.containsKey(username)) {
            if(!scoreJoker.get(username)) {
                scoreJoker.put(username, true);
            }
        }
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
     * This method will initialize the ConcurrentHashMap with the usernames of all players.
     */
    protected void initializeTimeJoker() {
        for(Map.Entry<String, Player> player : players.entrySet()) {
            timeJoker.remove(player.getKey());
        }
        for(Map.Entry<String, Player> player : players.entrySet()) {
            timeJoker.put(player.getKey(), 15000L);
        }

    }

    protected void initializeScoreJoker() {
        for(Map.Entry<String, Player> player : players.entrySet()) {
            scoreJoker.remove(player.getKey());
        }
        for(Map.Entry<String, Player> player : players.entrySet()) {
            scoreJoker.put(player.getKey(), false);
        }

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

    /**
     * retrieve the leaderboard of this game
     * @return leaderboard
     */
    public ConcurrentHashMap<String, Score> getLeaderboard() {
        return leaderboard;
    }

    /**
     * set the Score Leaderboard to server database
     * @param scoreController score database
     */
    public void setScoreController(ScoreController scoreController){
        this.scoreController = scoreController;
    }
}
