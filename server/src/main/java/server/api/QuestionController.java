package server.api;

import commons.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ActivityDB;
import server.database.ActivityDBController;
import server.database.QuestionDBController;

import java.util.*;

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
     *
     * @param random               the random to be used by this controller
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
     *
     * @return 200 OK: Question, 500 Internal Server Error if no question can be generated
     */
    @GetMapping("/random")
    public ResponseEntity<Question> getRandomQuestion() {

        ActivityDB activityDB = activityDBController.getInternalDB();

        long count = activityDB.count();
        int index;
        try {
            index = random.nextInt((int) count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.internalServerError().build();
        }

        Page<Activity> page = activityDB.findAll(PageRequest.of(index, 1));
        if (page.hasContent()) {
            Activity a = page.getContent().get(0);
            List<String> aw = new ArrayList<>();
            aw.add((int) ((getRandomWithExclusion(random, 0.5, 2, 1) * a.consumption)) + " Wh");
            aw.add( a.consumption + " Wh");
            aw.add((int) (((getRandomWithExclusion(random, 0.7, 2, 1) * a.consumption))) + " Wh");
            Collections.shuffle(aw);
            Question toReturn = new GeneralQuestion(a,aw,aw.indexOf(Long.toString(a.consumption)+" Wh"));
            questionDBController.add(toReturn);
            return ResponseEntity.ok(toReturn);
        }

        return ResponseEntity.internalServerError().build();

    }

    /**
     * Maps to /api/questions/random/which
     * Returns a which is more question generated from a random activities selected from the database
     *
     * @return 200 OK: Question, 500 Internal Server Error if no question can be generated
     */
    @GetMapping("/random/which")
    public ResponseEntity<Question> getWhichIsMoreQuestion() {

        try {
            List<Activity> activities = activityDBController.getThreeRandomActivities();

            Activity a1 = activities.get(0);
            for(int i=1;i<3;i++){
                if(a1.consumption<activities.get(i).consumption){
                    a1=activities.get(i);
                }
            }

            Question toReturn = new WhichIsMoreQuestion(activities, activities.indexOf(a1));
            questionDBController.add(toReturn);
            return ResponseEntity.ok(toReturn);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Maps to /api/questions/random/comparison
     * Returns a comparison question generated from a random activity selected from the database
     *
     * @return 200 OK: Question, 500 Internal Server Error if no question can be generated
     */
    @GetMapping("/random/comparison")
    public ResponseEntity<Question> getComparisonQuestion() {
        try{
            //We search for the smallest difference between two consumptions


            //First we sort the list of returned activities
            List<Activity> activities = activityDBController.getFiveRandomActivities();
            Collections.sort(activities, new Comparator<Activity>() {
                @Override
                public int compare(Activity o1, Activity o2) {
                    return (int) (o1.consumption - o2.consumption);
                }
            });

            //Now we search in the List
            Activity firstActivity = null;
            Activity secondActivity = null;
            int difference = Integer.MAX_VALUE;
            for (int i = 0; i < activities.size() - 1; i++) {
                if (activities.get(i + 1).consumption - activities.get(i).consumption < difference) {
                    difference = (int) (activities.get(i + 1).consumption - activities.get(i).consumption);
                    firstActivity = activities.get(i + 1);
                    secondActivity = activities.get(i);
                }
            }

            //If difference is bigger than 10% of the original activity we search again
            if (((double) difference / firstActivity.consumption) > 0.1) {
                return getComparisonQuestion();
            }

            //We return the question
            Question toReturn = new ComparisonQuestion(firstActivity, activities, activities.indexOf(secondActivity));
            questionDBController.add(toReturn);
            return ResponseEntity.ok(toReturn);
        }catch (StackOverflowError e){
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Maps to /api/questions/random/estimation
     * Returns a random question generated from a random activity selected from the database
     *
     * @return 200 OK: Question, 500 Internal Server Error if no question can be generated
     */
    @GetMapping("/random/estimation")
    public ResponseEntity<Question> getEstimationQuestion() {

        ActivityDB activityDB = activityDBController.getInternalDB();

        long count = activityDB.count();
        int index;
        try {
            index = random.nextInt((int) count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.internalServerError().build();
        }

        Page<Activity> page = activityDB.findAll(PageRequest.of(index, 1));
        if (page.hasContent()) {
            Activity a = page.getContent().get(0);
            Question toReturn = new EstimationQuestion(a);
            questionDBController.add(toReturn);
            return ResponseEntity.ok(toReturn);
        }

        return ResponseEntity.internalServerError().build();

    }

    /**
     * Maps to /api/questions/answer?questionID=id&answer=answer
     *
     * @param questionIDString the unique ID of the question being answered (post variable questionID)
     * @param answerString     the answer to be given to this question (either 1, 2, 3 etc. for multiple choice questions, or an integer for open questions)
     *                         (post variable answerString)
     * @return "200 OK: CORRECT", "200 OK: INCORRECT", or "200 OK: PROXIMITY: N" depending on the answer given,
     * 204 No Content if the question with the specified ID does not exist,
     * 400 Bad Request if the POST request is malformed,
     * or 500 Internal Server Error if the operation cannot be completed for any other reason
     */
    @PostMapping("/answer")
    public ResponseEntity<AnswerResponseEntity> answer(@RequestParam("questionID") String questionIDString,
                                                       @RequestParam("answer") String answerString) {

        long answer;
        try {
            answer = Long.parseLong(answerString);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        UUID questionID;
        try {
            questionID = UUID.fromString(questionIDString);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Question q = questionDBController.getById(questionID);
        if (Objects.isNull(q)) {
            return ResponseEntity.noContent().build();
        }

        if (q instanceof ComparisonQuestion || q instanceof GeneralQuestion || q instanceof WhichIsMoreQuestion) {
            if (answer == q.answer) {
                return ResponseEntity.ok(new AnswerResponseEntity(true));
            } else {
                return ResponseEntity.ok(new AnswerResponseEntity(false));
            }
        }

        if (q instanceof EstimationQuestion) {
            return ResponseEntity.ok(new AnswerResponseEntity((answer == q.answer), (answer - q.answer)));
        }

        return ResponseEntity.internalServerError().build();

    }


    /**
     * Generates random number from given range excluding those given as the parameter
     * @param rnd Instant of the Random class
     * @param start the start of the range in which the number will be generated
     * @param end the end of the range in which the number will be generated
     * @param exclude numbers that will be excluded from the given range
     * @return random number in given range (number is rounded to first decimal place)
     */
    public static double getRandomWithExclusion(Random rnd, double start, double end, int... exclude) {
        double random = start + (end - start) * rnd.nextDouble();
        random = (double) Math.round(random * 10) / 10;
        for (int ex : exclude) {
            if (random == (double) ex) {
                return getRandomWithExclusion(rnd, start, end, exclude);
            }
        }
        return random;
    }

}
