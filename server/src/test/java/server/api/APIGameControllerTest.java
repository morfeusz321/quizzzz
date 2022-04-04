package server.api;

import commons.*;
import commons.gameupdate.GameUpdate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.async.DeferredResult;
import server.game.Game;
import server.game.GameController;
import server.game.GameUpdateManager;
import server.game.QuestionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APIGameController.class)
class APIGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameController gameController;

    @Test
    void getAllQuestions() throws Exception {
        List<Activity> list = new ArrayList<>();
        list.add(new Activity("1", "/path/to/image/", "Activity 1", 9));
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            questions.add(new ComparisonQuestion(new Activity("1", "/path/to/image/", "Activity 1", 9), list, 1));
        }
        UUID uuid = new UUID(1122, 122);
        Game game = mock(Game.class);
        Mockito.when(gameController.getGame(uuid)).thenReturn(game);
        Mockito.when(game.getQuestions()).thenReturn(questions);


        String url = "/api/game/questions";
        mockMvc.perform(get(url).param("gameID", uuid.toString())).andExpect(status().isOk());


        verify(gameController, times(1)).getGame(uuid);
        verify(game, times(1)).getQuestions();
    }

    @Test
    void gameLongPollLoop() throws Exception {
        UUID uuid = new UUID(1122, 122);
        String username = "username";
        DeferredResult<ResponseEntity<GameUpdate>> result = new DeferredResult<>(40000L, ResponseEntity.internalServerError().build());
        Game game = mock(Game.class);
        QuestionGenerator questionGenerator = mock(QuestionGenerator.class);
        GameUpdateManager gameUpdateManager = mock(GameUpdateManager.class);
        Game game1 = new Game(gameUpdateManager, questionGenerator);

        Mockito.when(gameController.getGame(uuid)).thenReturn(game);
        Mockito.when(gameController.getCurrentGame()).thenReturn(game1);
        Mockito.when(game.containsPlayer(username)).thenReturn(true);
        doNothing().when(game).runDeferredResult(username, result);

        String url = "/api/game/";
        mockMvc.perform(get(url).param("gameID", uuid.toString()).param("username", username)).andExpect(status().isOk());

        verify(gameController, times(1)).getGame(uuid);
        verify(gameController, times(1)).getCurrentGame();
        verify(game, times(1)).containsPlayer(username);
    }

    @Test
    void answer() throws Exception {
        UUID uuid = new UUID(1122, 122);
        String username = "username";
        DeferredResult<ResponseEntity<GameUpdate>> result = new DeferredResult<>(40000L, ResponseEntity.internalServerError().build());
        Game game = mock(Game.class);
        QuestionGenerator questionGenerator = mock(QuestionGenerator.class);
        GameUpdateManager gameUpdateManager = mock(GameUpdateManager.class);
        Game game1 = new Game(gameUpdateManager, questionGenerator);
        String answer = "1";

        Mockito.when(gameController.getGame(uuid)).thenReturn(game);
        Mockito.when(gameController.getCurrentGame()).thenReturn(game1);
        Mockito.when(game.containsPlayer(username)).thenReturn(true);
        doNothing().when(game).saveAnswer(username, Long.parseLong(answer));

        String url = "/api/game/answer";
        mockMvc.perform(post(url).param("gameID", uuid.toString()).param("playerName", username).param("answer", answer)).andExpect(status().isOk());

        verify(gameController, times(1)).getGame(uuid);
        verify(gameController, times(1)).getCurrentGame();
        verify(game, times(1)).saveAnswer(username, Long.parseLong(answer));
    }
}