package server.api;

import commons.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.QuestionDB;

import java.util.Random;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final Random random;
    private final QuestionDB questionDB;

    public QuestionController(Random random, QuestionDB questionDB) {

        this.random = random;
        this.questionDB = questionDB;

    }

    @GetMapping("/random")
    public ResponseEntity<Question> getRandomQuestion() {

        long count = questionDB.count();
        int index = random.nextInt((int) count);

        Page<Question> page = questionDB.findAll(PageRequest.of(index, 1));
        if(page.hasContent()) {
            return ResponseEntity.ok(page.getContent().get(0));
        }

        return ResponseEntity.internalServerError().build();

    }

}
