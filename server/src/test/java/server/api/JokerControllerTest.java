package server.api;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import server.game.Game;
import server.game.GameController;


import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JokerController.class)
class JokerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameController gameController;

    @Test
    void useTimeJoker() throws Exception {
        String username = "username";
        UUID uuid = new UUID(1122, 122);
        Game game = mock(Game.class);
        Mockito.when(gameController.getGame(uuid)).thenReturn(game);
        Mockito.doNothing().when(game).useTimeJoker(username);


        String url = "/api/jokers/time";
        mockMvc.perform(post(url).param("gameUUID", uuid.toString()).param("username", username)).andExpect(status().isOk());


        verify(gameController, times(1)).getGame(uuid);
        verify(game, times(1)).useTimeJoker(username);
    }

    @Test
    void useTimeJokerGameNull() throws Exception {
        String username = "username";
        UUID uuid = new UUID(1122, 122);
        Mockito.when(gameController.getGame(uuid)).thenReturn(null);


        String url = "/api/jokers/time";
        mockMvc.perform(post(url).param("gameUUID", uuid.toString()).param("username", username)).andExpect(status().isBadRequest());


        verify(gameController, times(1)).getGame(uuid);
    }

    @Test
    void useTimeJokerBadUUID() throws Exception {
        String username = "username";

        String url = "/api/jokers/time";
        mockMvc.perform(post(url).param("gameUUID", "badthing").param("username", username)).andExpect(status().isBadRequest());

    }


    @Test
    void useAnswerJoker() throws Exception {
        String username = "username";
        UUID uuid = new UUID(1122, 122);
        Game game = mock(Game.class);
        Mockito.when(gameController.getGame(uuid)).thenReturn(game);
        Mockito.doNothing().when(game).useQuestionJoker(username);


        String url = "/api/jokers/question";
        mockMvc.perform(post(url).param("gameUUID", uuid.toString()).param("username", username)).andExpect(status().isOk());


        verify(gameController, times(1)).getGame(uuid);
        verify(game, times(1)).useQuestionJoker(username);
    }

    @Test
    void useQuestionJokerGameNull() throws Exception {
        String username = "username";
        UUID uuid = new UUID(1122, 122);
        Mockito.when(gameController.getGame(uuid)).thenReturn(null);


        String url = "/api/jokers/question";
        mockMvc.perform(post(url).param("gameUUID", uuid.toString()).param("username", username)).andExpect(status().isBadRequest());


        verify(gameController, times(1)).getGame(uuid);
    }

    @Test
    void useQuestionJokerBadUUID() throws Exception {
        String username = "username";

        String url = "/api/jokers/question";
        mockMvc.perform(post(url).param("gameUUID", "badthing").param("username", username)).andExpect(status().isBadRequest());

    }

    @Test
    void useScoreJoker() throws Exception {
        String username = "username";
        UUID uuid = new UUID(1122, 122);
        Game game = mock(Game.class);
        Mockito.when(gameController.getGame(uuid)).thenReturn(game);
        Mockito.doNothing().when(game).useScoreJoker(username);


        String url = "/api/jokers/score";
        mockMvc.perform(post(url).param("gameUUID", uuid.toString()).param("username", username)).andExpect(status().isOk());


        verify(gameController, times(1)).getGame(uuid);
        verify(game, times(1)).useScoreJoker(username);
    }

    @Test
    void useScoreJokerGameNull() throws Exception {
        String username = "username";
        UUID uuid = new UUID(1122, 122);
        Mockito.when(gameController.getGame(uuid)).thenReturn(null);


        String url = "/api/jokers/score";
        mockMvc.perform(post(url).param("gameUUID", uuid.toString()).param("username", username)).andExpect(status().isBadRequest());


        verify(gameController, times(1)).getGame(uuid);
    }

    @Test
    void useScoreJokerBadUUID() throws Exception {
        String username = "username";

        String url = "/api/jokers/score";
        mockMvc.perform(post(url).param("gameUUID", "badthing").param("username", username)).andExpect(status().isBadRequest());

    }

}