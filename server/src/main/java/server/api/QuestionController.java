package server.api;

import commons.Activity;
import commons.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ActivityDB;
import server.database.ActivityDBController;

import java.util.Random;

/**
 * The API controller for the questions. Controls everything mapped to /api/questions/...
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final Random random;
    private final ActivityDBController activityDBController;

    /**
     * Creates the API controller
     * @param random the random to be used by this controller
     * @param activityDBController the interface with the activity database to be used by this controller
     */
    public QuestionController(Random random, ActivityDBController activityDBController) {

        this.random = random;
        this.activityDBController = activityDBController;

    }

    /**
     * Maps to /api/questions/random
     * Returns a random question generated from a random activity selected from the database
     * @return a randomly generated question
     */
    @GetMapping("/random")
    public ResponseEntity<Question> getRandomQuestion() {

        ActivityDB activityDB = activityDBController.getInternalDB();

        long count = activityDB.count();
        int index = random.nextInt((int) count);

        Page<Activity> page = activityDB.findAll(PageRequest.of(index, 1));
        if(page.hasContent()) {
            return ResponseEntity.ok(new Question(page.getContent().get(0)));
        }

        return ResponseEntity.internalServerError().build();

    }

}
