package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jokers")
public class JokerController {
    /**
     * empty
     * @param username empty
     */
    @PostMapping("/time")
    public ResponseEntity<String> test(@RequestParam("username") String username) {
        System.out.println(username);
        return ResponseEntity.ok(username);
    }
}
