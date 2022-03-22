package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    /**
     * The mapping to the root of the server should always return 200 OK: Quizzz Server to indicate
     * to potential clients that it is, in fact, a Quizzz Server
     * @return 200 OK: Quizzz Server
     */
    @GetMapping("/")
    public ResponseEntity<String> serverExists() {

        return ResponseEntity.ok("Quizzz Server");

    }

}
