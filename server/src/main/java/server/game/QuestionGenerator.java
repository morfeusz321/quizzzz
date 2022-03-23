package server.game;

import commons.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import server.database.ActivityDB;
import server.database.ActivityDBController;
import server.database.QuestionDBController;

import java.util.*;

@Component
public class QuestionGenerator {
    private final Random random;
    private final ActivityDBController activityDBController;
    private final QuestionDBController questionDBController;

    /**
     * Creates the question generator
     * @param random               the random number generator to be used by this controller
     * @param activityDBController the interface with the activity database to be used for generation
     * @param questionDBController the interface with the question database to be used for generation
     */
    public QuestionGenerator(Random random, ActivityDBController activityDBController, QuestionDBController questionDBController) {

        this.random = random;
        this.activityDBController = activityDBController;
        this.questionDBController = questionDBController;

    }

    /**
     * Returns a random question (of a random type) generated from a random activity selected from the database
     * @return A Question, or null if no question can be generated
     */
    public Question getRandomQuestion() {

        int type = random.nextInt(4);
        return switch (type) {
            case 0 -> getGeneralQuestion();
            case 1 -> getWhichIsMoreQuestion();
            case 2 -> getComparisonQuestion();
            case 3 -> getEstimationQuestion();
            default -> null;
        };

    }

    /**
     * Returns a random general question generated from a random activity selected from the database
     *
     * @return A GeneralQuestion, or null if no question can be generated
     */
    public Question getGeneralQuestion() {

        CommonUtils utils = new CommonUtils();

        ActivityDB activityDB = activityDBController.getInternalDB();

        long count = activityDB.count();
        int index;
        try {
            index = random.nextInt((int) count);
        } catch (IllegalArgumentException e) {
            return null;
        }

        Page<Activity> page = activityDB.findAll(PageRequest.of(index, 1));
        if (page.hasContent()) {
            Activity a = page.getContent().get(0);
            List<String> aw = new ArrayList<>();
            aw.add((int) ((utils.getRandomWithExclusion(random, 0.5, 2, 1) * a.consumption)) + " Wh");
            aw.add( a.consumption + " Wh");
            aw.add((int) (((utils.getRandomWithExclusion(random, 0.7, 2, 1) * a.consumption))) + " Wh");
            Collections.shuffle(aw);
            Question toReturn = new GeneralQuestion(a,aw,aw.indexOf(Long.toString(a.consumption)+" Wh"));
            questionDBController.add(toReturn);
            return toReturn;
        }

        return null;

    }

    /**
     * Returns a which is more question generated from a random activities selected from the database
     *
     * @return A WhichIsMoreQuestion, or null if no question can be generated
     */
    public Question getWhichIsMoreQuestion() {

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
            return toReturn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a comparison question generated from a random activity selected from the database
     *
     * @return A ComparisonQuestion, or null if no question can be generated
     */
    public Question getComparisonQuestion() {
        try{
            //We search for the smallest difference between two consumptions


            //First we sort the list of returned activities
            List<Activity> activities = activityDBController.getFiveRandomActivities();

            if(activities.size() < 5) {
                return null;
            }

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
            return toReturn;
        } catch (StackOverflowError e){
            return null;
        }
    }

    /**
     * Returns a random estimation question generated from a random activity selected from the database
     *
     * @return An EstimationQuestion, or null if no question can be generated
     */
    public Question getEstimationQuestion() {

        ActivityDB activityDB = activityDBController.getInternalDB();

        long count = activityDB.count();
        int index;
        try {
            index = random.nextInt((int) count);
        } catch (IllegalArgumentException e) {
            return null;
        }

        Page<Activity> page = activityDB.findAll(PageRequest.of(index, 1));
        if (page.hasContent()) {
            Activity a = page.getContent().get(0);
            Question toReturn = new EstimationQuestion(a);
            questionDBController.add(toReturn);
            return toReturn;
        }

        return null;

    }

    /**
     * Generates 20 questions for a game, with a minimum amount of questions per question type.
     * @param minPerQuestionType The minimum amount of questions per question type
     * @throws IllegalArgumentException Throws an exception if the minimum amount of questions per type
     * is not valid (i.e. > 5 because we only have 20 questions, or < 0)
     * @return The generated list of questions, or null if something went wrong
     */
    public List<Question> generateGameQuestions(int minPerQuestionType) throws IllegalArgumentException {
        if(minPerQuestionType > 5 || minPerQuestionType < 0) {
            throw new IllegalArgumentException();
        }

        List<Question> questions = new ArrayList<>();

        // Generate the minimum amount of questions per question type
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < minPerQuestionType; j++){
                Question generated = switch (i) {
                    case 0 -> getGeneralQuestion();
                    case 1 -> getComparisonQuestion();
                    case 2 -> getEstimationQuestion();
                    case 3 -> getWhichIsMoreQuestion();
                    default -> null; // This is only executed if something went wrong, it should not be called.
                };
                if(generated == null){
                    // Something went wrong
                    return null;
                }
                questions.add(generated);
            }
        }

        // Generate the questions with random types
        for(int i = 0; i < 20 - (4 * minPerQuestionType); i++){
            Question generated = getRandomQuestion();
            if(generated == null){
                // Something went wrong
                return null;
            }
            questions.add(generated);
        }

        // The first questions are ordered per type, so shuffle the question list
        Collections.shuffle(questions);
        return questions;
    }

    // TODO: the following section is commented out so that we still have a reference for receiving answers. As
    //  soon as receiving answers is implemented, we should remove this.

    /*
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

    /*
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
    */
}
