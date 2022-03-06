package server.api;

import commons.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ActivityDB;
import server.database.ActivityDBController;
import server.database.QuestionDBController;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * The API controller for the questions. Controls everything mapped to /api/questions/...
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final Random random;
    private final ActivityDBController activityDBController;
    private final QuestionDBController questionDBController;

    /**
     * Creates the API controller
     * @param random the random to be used by this controller
     * @param activityDBController the interface with the activity database to be used by this controller
     * @param questionDBController the interface with the question database to be used by this controller
     */
    public QuestionController(Random random, ActivityDBController activityDBController, QuestionDBController questionDBController) {

        this.random = random;
        this.activityDBController = activityDBController;
        this.questionDBController = questionDBController;

    }

    /**
     * Maps to /api/questions/random
     * Returns a random question generated from a random activity selected from the database
     * @return 200 OK: Question, 500 Internal Server Error if no question can be generated
     */
    @GetMapping("/random")
    public ResponseEntity<Question> getRandomQuestion() {

        ActivityDB activityDB = activityDBController.getInternalDB();

        long count = activityDB.count();
        int index;
        try {
            index = random.nextInt((int) count);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.internalServerError().build();
        }

        Page<Activity> page = activityDB.findAll(PageRequest.of(index, 1));
        if(page.hasContent()) {
            Activity a = page.getContent().get(0);
            Question toReturn = new GeneralQuestion(a,
                    List.of((0.5 * a.consumption) + " Wh", a.consumption + " Wh", (2 * a.consumption) + " Wh"),
                    2);
            questionDBController.add(toReturn);
            return ResponseEntity.ok(toReturn);
        }

        return ResponseEntity.internalServerError().build();

    }

    /**
     * Maps to /api/questions/answer?questionID=id&answer=answer
     * @param questionIDString the unique ID of the question being answered (post variable questionID)
     * @param answerString the answer to be given to this question (either 1, 2, 3 etc. for multiple choice questions, or an integer for open questions)
     *                     (post variable answerString)
     * @return "200 OK: CORRECT", "200 OK: INCORRECT", or "200 OK: PROXIMITY: N" depending on the answer given,
     *                      204 No Content if the question with the specified ID does not exist,
     *                      400 Bad Request if the POST request is malformed,
     *                      or 500 Internal Server Error if the operation cannot be completed for any other reason
     */
    @PostMapping("/answer")
    public ResponseEntity<AnswerResponseEntity> answer(@RequestParam("questionID") String questionIDString,
                                                       @RequestParam("answer") String answerString) {

        long answer;
        try {
            answer = Long.parseLong(answerString);
        } catch(NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        UUID questionID;
        try {
            questionID = UUID.fromString(questionIDString);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Question q = questionDBController.getById(questionID);
        if(Objects.isNull(q)) {
            return ResponseEntity.noContent().build();
        }

        if(q instanceof ComparisonQuestion || q instanceof GeneralQuestion) {
            if(answer == q.answer) {
                return ResponseEntity.ok(new AnswerResponseEntity(true));
            } else {
                return ResponseEntity.ok(new AnswerResponseEntity(false));
            }
        }

        if(q instanceof EstimationQuestion) {
            return ResponseEntity.ok(new AnswerResponseEntity((answer == q.answer), (answer - q.answer)));
        }

        return ResponseEntity.internalServerError().build();

    }

}
