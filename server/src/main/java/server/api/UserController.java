package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import commons.Player;

/**
 * The API controller for the user. Controls everything mapped to /api/user/...
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * Creates the API controller
     */
    public UserController() {
    }

    /**
     * Maps to /api/user/enter
     * Creates the player entity and sets the username for it
     * @return a text that the user has entered the name successfully
     */
    @PostMapping("/enter")
    public ResponseEntity<String> getRandomQuestion(@RequestBody String username) {

        Player player = new Player();
        player.setUsername(username);

        return ResponseEntity.ok("Entered a name successfully!");

    }

}
