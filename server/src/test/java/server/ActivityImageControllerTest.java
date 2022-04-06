package server;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import server.api.AdminInterfaceController;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@WebMvcTest(ActivityImageController.class)
class ActivityImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getImage() throws URISyntaxException {
        String imagePathGroup = "imagePathGroup";
        String imagePathFileName = "imagePathFileName";
        URI uri = mock(URI.class);
        Mockito.when(ActivityImageController.class.getClassLoader().getResource("activities/" + URLDecoder.decode(imagePathGroup, StandardCharsets.UTF_8) + "/" + URLDecoder.decode(imagePathFileName, StandardCharsets.UTF_8)).toURI()).thenReturn(uri);
    }
}