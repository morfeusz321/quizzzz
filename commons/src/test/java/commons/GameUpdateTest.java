package commons;

import commons.gameupdate.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameUpdateTest {

    private Player player1;
    private Player player2;
    private List<Player> playerList;

    private UUID gameUUID;

    private List<Score> testScoreList;
    private AnswerResponseEntity testAnswerResponseEntity;

    private GameUpdate gameUpdateDisplayLeaderboard;
    private GameUpdate gameUpdateFullPlayerList;
    private GameUpdate gameUpdateGameFinished;
    private GameUpdate gameUpdateGameStarting;
    private GameUpdate gameUpdateNameInUse;
    private GameUpdate gameUpdateNextQuestion;
    private GameUpdate gameUpdatePlayerJoined;
    private GameUpdate gameUpdatePlayerLeft;
    private GameUpdate gameUpdateTransitionPeriodEntered;
    private GameUpdate gameUpdateNoQuestions;

    @BeforeEach
    public void setup() {

        this.player1 = new Player("P1");
        this.player2 = new Player("P2");
        this.playerList = List.of(player1, player2);

        this.gameUUID = UUID.randomUUID();

        this.testScoreList = new ArrayList<>(List.of(new Score("P1", 500), new Score("P2", 100)));
        this.testAnswerResponseEntity = new AnswerResponseEntity(true, 1);

        this.gameUpdateDisplayLeaderboard = new GameUpdateDisplayLeaderboard(testScoreList);
        this.gameUpdateFullPlayerList = new GameUpdateFullPlayerList(playerList, gameUUID);
        this.gameUpdateGameFinished = new GameUpdateGameFinished(testScoreList);
        this.gameUpdateGameStarting = new GameUpdateGameStarting();
        this.gameUpdateNameInUse = new GameUpdateNameInUse();
        this.gameUpdateNextQuestion = new GameUpdateNextQuestion(0);
        this.gameUpdatePlayerJoined = new GameUpdatePlayerJoined(player1);
        this.gameUpdatePlayerLeft = new GameUpdatePlayerLeft(player1);
        this.gameUpdateTransitionPeriodEntered = new GameUpdateTransitionPeriodEntered(testAnswerResponseEntity);
        this.gameUpdateNoQuestions = new GameUpdateNoQuestions();

    }

    @Test
    public void testConstructor() {

        assertNotNull(gameUpdateDisplayLeaderboard);
        assertEquals(testScoreList, ((GameUpdateDisplayLeaderboard) gameUpdateDisplayLeaderboard).getLeaderboard());

        assertNotNull(gameUpdateFullPlayerList);
        assertEquals(List.of(player1, player2), ((GameUpdateFullPlayerList) gameUpdateFullPlayerList).getPlayerList());
        assertEquals(gameUUID, ((GameUpdateFullPlayerList) gameUpdateFullPlayerList).getGameUUID());

        assertNotNull(gameUpdateGameFinished);
        assertEquals(testScoreList, ((GameUpdateGameFinished) gameUpdateGameFinished).getLeaderboard());

        assertNotNull(gameUpdateGameStarting);

        assertNotNull(gameUpdateNameInUse);

        assertNotNull(gameUpdateNextQuestion);
        assertEquals(0, ((GameUpdateNextQuestion) gameUpdateNextQuestion).getQuestionIdx());

        assertNotNull(gameUpdatePlayerJoined);
        assertEquals(player1, ((GameUpdatePlayerJoined) gameUpdatePlayerJoined).getPlayer());

        assertNotNull(gameUpdatePlayerLeft);
        assertEquals(player1, ((GameUpdatePlayerLeft) gameUpdatePlayerLeft).getPlayer());

        assertNotNull(gameUpdateTransitionPeriodEntered);
        assertEquals(testAnswerResponseEntity, ((GameUpdateTransitionPeriodEntered) gameUpdateTransitionPeriodEntered).getAnswerResponseEntity());

        assertNotNull(gameUpdateNoQuestions);

    }

    @Test
    public void testEqualsAndHashCodeEqual() {

        GameUpdate gameUpdateDisplayLeaderboard2 = new GameUpdateDisplayLeaderboard(testScoreList);
        GameUpdate gameUpdateFullPlayerList2 = new GameUpdateFullPlayerList(playerList, gameUUID);
        GameUpdate gameUpdateGameFinished2 = new GameUpdateGameFinished(testScoreList);
        GameUpdate gameUpdateGameStarting2 = new GameUpdateGameStarting();
        GameUpdate gameUpdateNameInUse2 = new GameUpdateNameInUse();
        GameUpdate gameUpdateNextQuestion2 = new GameUpdateNextQuestion(0);
        GameUpdate gameUpdatePlayerJoined2 = new GameUpdatePlayerJoined(player1);
        GameUpdate gameUpdatePlayerLeft2 = new GameUpdatePlayerLeft(player1);
        GameUpdate gameUpdateTransitionPeriodEntered2 = new GameUpdateTransitionPeriodEntered(testAnswerResponseEntity);
        GameUpdate gameUpdateNoQuestions2 = new GameUpdateNoQuestions();

        assertEquals(gameUpdateDisplayLeaderboard, gameUpdateDisplayLeaderboard2);
        assertEquals(gameUpdateFullPlayerList, gameUpdateFullPlayerList2);
        assertEquals(gameUpdateGameFinished, gameUpdateGameFinished2);
        assertEquals(gameUpdateGameStarting, gameUpdateGameStarting2);
        assertEquals(gameUpdateNameInUse, gameUpdateNameInUse2);
        assertEquals(gameUpdateNextQuestion, gameUpdateNextQuestion2);
        assertEquals(gameUpdatePlayerJoined, gameUpdatePlayerJoined2);
        assertEquals(gameUpdatePlayerLeft, gameUpdatePlayerLeft2);
        assertEquals(gameUpdateTransitionPeriodEntered, gameUpdateTransitionPeriodEntered2);
        assertEquals(gameUpdateNoQuestions, gameUpdateNoQuestions2);

        assertEquals(gameUpdateDisplayLeaderboard.hashCode(), gameUpdateDisplayLeaderboard2.hashCode());
        assertEquals(gameUpdateFullPlayerList.hashCode(), gameUpdateFullPlayerList2.hashCode());
        assertEquals(gameUpdateGameFinished.hashCode(), gameUpdateGameFinished2.hashCode());
        assertEquals(gameUpdateGameStarting.hashCode(), gameUpdateGameStarting2.hashCode());
        assertEquals(gameUpdateNameInUse.hashCode(), gameUpdateNameInUse2.hashCode());
        assertEquals(gameUpdateNextQuestion.hashCode(), gameUpdateNextQuestion2.hashCode());
        assertEquals(gameUpdatePlayerJoined.hashCode(), gameUpdatePlayerJoined2.hashCode());
        assertEquals(gameUpdatePlayerLeft.hashCode(), gameUpdatePlayerLeft2.hashCode());
        assertEquals(gameUpdateTransitionPeriodEntered.hashCode(), gameUpdateTransitionPeriodEntered2.hashCode());
        assertEquals(gameUpdateNoQuestions.hashCode(), gameUpdateNoQuestions2.hashCode());

    }

    @Test
    public void testEqualsAndHashCodeNotEqual() {

        GameUpdate gameUpdateDisplayLeaderboard2 = new GameUpdateDisplayLeaderboard(List.of(new Score("P3", 50)));
        GameUpdate gameUpdateFullPlayerList2 = new GameUpdateFullPlayerList(List.of(new Player("P3")), gameUUID);
        GameUpdate gameUpdateFullPlayerList3 = new GameUpdateFullPlayerList(playerList, UUID.randomUUID());
        GameUpdate gameUpdateGameFinished2 = new GameUpdateGameFinished(List.of(new Score("P3", 50)));
        GameUpdate gameUpdateNextQuestion2 = new GameUpdateNextQuestion(1);
        GameUpdate gameUpdatePlayerJoined2 = new GameUpdatePlayerJoined(new Player("P3"));
        GameUpdate gameUpdatePlayerLeft2 = new GameUpdatePlayerLeft(new Player("P3"));
        GameUpdate gameUpdateTransitionPeriodEntered2 = new GameUpdateTransitionPeriodEntered(new AnswerResponseEntity(false, 1));

        assertNotEquals(gameUpdateDisplayLeaderboard, gameUpdateDisplayLeaderboard2);
        assertNotEquals(gameUpdateFullPlayerList, gameUpdateFullPlayerList2);
        assertNotEquals(gameUpdateFullPlayerList, gameUpdateFullPlayerList3);
        assertNotEquals(gameUpdateGameFinished, gameUpdateGameFinished2);
        assertNotEquals(gameUpdateNextQuestion, gameUpdateNextQuestion2);
        assertNotEquals(gameUpdatePlayerJoined, gameUpdatePlayerJoined2);
        assertNotEquals(gameUpdatePlayerLeft, gameUpdatePlayerLeft2);
        assertNotEquals(gameUpdateTransitionPeriodEntered, gameUpdateTransitionPeriodEntered2);

        assertNotEquals(gameUpdateDisplayLeaderboard.hashCode(), gameUpdateDisplayLeaderboard2.hashCode());
        assertNotEquals(gameUpdateFullPlayerList.hashCode(), gameUpdateFullPlayerList2.hashCode());
        assertNotEquals(gameUpdateFullPlayerList.hashCode(), gameUpdateFullPlayerList3.hashCode());
        assertNotEquals(gameUpdateGameFinished.hashCode(), gameUpdateGameFinished2.hashCode());
        assertNotEquals(gameUpdateNextQuestion.hashCode(), gameUpdateNextQuestion2.hashCode());
        assertNotEquals(gameUpdatePlayerJoined.hashCode(), gameUpdatePlayerJoined2.hashCode());
        assertNotEquals(gameUpdatePlayerLeft.hashCode(), gameUpdatePlayerLeft2.hashCode());
        assertNotEquals(gameUpdateTransitionPeriodEntered.hashCode(), gameUpdateTransitionPeriodEntered2.hashCode());

    }

    @Test
    public void testToString() {

        String gameUpdateDisplayLeaderboardToString = gameUpdateDisplayLeaderboard.toString();
        assertTrue(gameUpdateDisplayLeaderboardToString.contains(GameUpdateDisplayLeaderboard.class.getSimpleName()));
        assertTrue(gameUpdateDisplayLeaderboardToString.contains("\n"));
        assertTrue(gameUpdateDisplayLeaderboardToString.contains("leaderboard"));

        String gameUpdateFullPlayerListToString = gameUpdateFullPlayerList.toString();
        assertTrue(gameUpdateFullPlayerListToString.contains(GameUpdateFullPlayerList.class.getSimpleName()));
        assertTrue(gameUpdateFullPlayerListToString.contains("\n"));
        assertTrue(gameUpdateFullPlayerListToString.contains("playerList"));
        assertTrue(gameUpdateFullPlayerListToString.contains("gameUUID"));

        String gameUpdateGameFinishedToString = gameUpdateGameFinished.toString();
        assertTrue(gameUpdateGameFinishedToString.contains(GameUpdateGameFinished.class.getSimpleName()));
        assertTrue(gameUpdateGameFinishedToString.contains("\n"));
        assertTrue(gameUpdateGameFinishedToString.contains("leaderboard"));

        String gameUpdateGameStartingToString = gameUpdateGameStarting.toString();
        assertTrue(gameUpdateGameStartingToString.contains(GameUpdateGameStarting.class.getSimpleName()));
        assertTrue(gameUpdateGameStartingToString.contains("\n"));

        String gameUpdateNameInUseToString = gameUpdateNameInUse.toString();
        assertTrue(gameUpdateNameInUseToString.contains(GameUpdateNameInUse.class.getSimpleName()));
        assertTrue(gameUpdateNameInUseToString.contains("\n"));

        String gameUpdateNextQuestionToString = gameUpdateNextQuestion.toString();
        assertTrue(gameUpdateNextQuestionToString.contains(GameUpdateNextQuestion.class.getSimpleName()));
        assertTrue(gameUpdateNextQuestionToString.contains("\n"));
        assertTrue(gameUpdateNextQuestionToString.contains("questionIdx"));

        String gameUpdatePlayerJoinedToString = gameUpdatePlayerJoined.toString();
        assertTrue(gameUpdatePlayerJoinedToString.contains(GameUpdatePlayerJoined.class.getSimpleName()));
        assertTrue(gameUpdatePlayerJoinedToString.contains("\n"));
        assertTrue(gameUpdatePlayerJoinedToString.contains("player"));

        String gameUpdatePlayerLeftToString = gameUpdatePlayerLeft.toString();
        assertTrue(gameUpdatePlayerLeftToString.contains(GameUpdatePlayerLeft.class.getSimpleName()));
        assertTrue(gameUpdatePlayerLeftToString.contains("\n"));
        assertTrue(gameUpdatePlayerLeftToString.contains("player"));

        String gameUpdateTransitionPeriodEnteredToString = gameUpdateTransitionPeriodEntered.toString();
        assertTrue(gameUpdateTransitionPeriodEnteredToString.contains(GameUpdateTransitionPeriodEntered.class.getSimpleName()));
        assertTrue(gameUpdateTransitionPeriodEnteredToString.contains("\n"));
        assertTrue(gameUpdateTransitionPeriodEnteredToString.contains("answerResponseEntity"));

        String gameUpdateNoQuestionsToString = gameUpdateNoQuestions.toString();
        assertTrue(gameUpdateNoQuestionsToString.contains(GameUpdateNoQuestions.class.getSimpleName()));
        assertTrue(gameUpdateNoQuestionsToString.contains("\n"));

    }

}
