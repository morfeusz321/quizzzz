package server.game.questions;

import commons.*;
import org.springframework.stereotype.Component;
import server.database.ActivityDBController;
import server.database.QuestionDBController;

import java.util.*;

@Component
public class QuestionGenerator {
    private final Random random;
    private final ActivityDBController activityDBController;
    private final QuestionDBController questionDBController;
    private final QuestionGeneratorUtils utils;

    /**
     * Creates the question generator
     * @param random               the random number generator to be used by this controller
     * @param activityDBController the interface with the activity database to be used for generation
     * @param questionDBController the interface with the question database to be used for generation
     * @param utils                instance of utility class for question generation
     */
    public QuestionGenerator(Random random,
                             ActivityDBController activityDBController,
                             QuestionDBController questionDBController,
                             QuestionGeneratorUtils utils) {

        this.random = random;
        this.activityDBController = activityDBController;
        this.questionDBController = questionDBController;
        this.utils = utils;

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

        try {

            Activity a = activityDBController.getRandomActivity();
            if(a == null) {
                return null; // Something went wrong when trying to retrieve an activity.
            }

            String mainConsumptionString = utils.createConsumptionString(a.consumption);
            long[] consumptions = new long[2];
            long[] bounds = utils.getBoundsGeneralQuestion(a.consumption);

            // Generate the first answer option
            if(!utils.checkIfGeneratable(List.of(a.consumption), 0.1, bounds[0], bounds[1])){
                // Might generate StackOverFlowErrors, try again
                return getGeneralQuestion();
            }
            consumptions[0] = utils.randomLongInRangeExcl(
                    bounds[0],
                    bounds[1],
                    random,
                    List.of(mainConsumptionString),
                    List.of(a.consumption),
                    0.1
            );
            String secondConsumptionString = utils.createConsumptionString(consumptions[0]);

            // Generate the second answer option
            if(!utils.checkIfGeneratable(List.of(a.consumption, consumptions[0]), 0.1, bounds[0], bounds[1])){
                // Might generate StackOverFlowErrors, try again
                return getGeneralQuestion();
            }
            consumptions[1] = utils.randomLongInRangeExcl(
                    bounds[0],
                    bounds[1],
                    random,
                    List.of(mainConsumptionString, secondConsumptionString),
                    List.of(a.consumption, consumptions[0]),
                    0.1
            );

            List<String> aw = new ArrayList<>();
            aw.add(mainConsumptionString);
            aw.add(secondConsumptionString);
            aw.add(utils.createConsumptionString(consumptions[1]));
            Collections.shuffle(aw);

            Question toReturn = new GeneralQuestion(a, aw, aw.indexOf(mainConsumptionString) + 1);
            questionDBController.add(toReturn);

            return toReturn;

        } catch (StackOverflowError e) {
            // No good number could be found in the generated range, try again
            // This should not be called, as the safety check should prohibit this
            e.printStackTrace();
            return getGeneralQuestion();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Returns a which is more question generated from a random activities selected from the database
     *
     * @return A WhichIsMoreQuestion, or null if no question can be generated
     */
    public Question getWhichIsMoreQuestion() {
        try {
            List<Activity> activities = new ArrayList<>();
            // Get first activity: No conditions.
            Activity first = activityDBController.getRandomActivity();
            if(first == null) {
                return null; // Something went wrong when trying to retrieve an activity.
            }
            activities.add(first);
            // Second activity: Bounds depend on first activity added. The id and consumption of the first activity are
            // excluded.
            long[] bounds = utils.getLowerUpperBoundSmall(first.consumption);
            activities.add(activityDBController.getActivityExclAndInRange(
                    List.of(first.id),
                    List.of(first.consumption),
                    bounds[0],
                    bounds[1]
            ));
            if(activities.get(1) == null) {
                return getWhichIsMoreQuestion(); // The boundaries did not include a fitting activity. Try again.
            }
            // Third activity: Bounds would depend on the average of the first and second activity, those are already
            // in the correct range, however. The ids and consumptions of the previous activities are excluded.
            activities.add(activityDBController.getActivityExclAndInRange(
                    List.of(first.id, activities.get(1).id),
                    List.of(first.consumption, activities.get(1).consumption),
                    bounds[0],
                    bounds[1]
            ));
            if(activities.get(2) == null) {
                return getWhichIsMoreQuestion(); // The boundaries did not include a fitting activity. Try again.
            }

            Activity a1 = activities.get(0);
            for(int i=1;i<3;i++){
                if(a1.consumption<activities.get(i).consumption){
                    a1=activities.get(i);
                }
            }

            Question toReturn = new WhichIsMoreQuestion(activities, activities.indexOf(a1)+1);
            questionDBController.add(toReturn);
            return toReturn;
        } catch (StackOverflowError e){
            System.out.println("Error: No valid question could be generated from the database.");
            return null;
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

            if(activities.size() < 5 || activities.contains(null)) {
                return null;
            }

            Collections.sort(activities, new Comparator<Activity>() {
                @Override
                public int compare(Activity o1, Activity o2) {
                    // this cannot simply return the difference as the difference can be a long
                    long diff = o1.consumption - o2.consumption;
                    if(diff < 0){
                        return -1;
                    } else if (diff > 0){
                        return 1;
                    }
                    return 0;
                }
            });

            //Now we search in the List
            Activity firstActivity = null;
            Activity secondActivity = null;
            long difference = Long.MAX_VALUE;
            for (int i = 0; i < activities.size() - 1; i++) {
                if (activities.get(i + 1).consumption - activities.get(i).consumption < difference) {
                    difference = activities.get(i + 1).consumption - activities.get(i).consumption;
                    firstActivity = activities.get(i + 1);
                    secondActivity = activities.get(i);
                }
            }

            // If difference is bigger than 10% of the original activity we search again
            // Note: Here casting to double is fine, because doubles have a bigger range than longs
            if (((double) difference / firstActivity.consumption) > 0.1) {
                return getComparisonQuestion();
            }

            // Create answer options list (activities can not be used as answer options list, as it contains 5
            // activities, including the actual answer and the title. The actual answer should be guaranteed to be in
            // the answer options, and the title should never be in it.
            List<Activity> answerOptions = new ArrayList<>();
            // Do not add the first activity as this is the title (should not be a selectable answer)
            answerOptions.add(secondActivity);
            activities.remove(firstActivity);
            activities.remove(secondActivity);
            // Get two random activities from the remaining one's
            int randomIdx = random.nextInt(3);
            answerOptions.add(activities.remove(randomIdx));
            randomIdx = random.nextInt(2);
            answerOptions.add(activities.remove(randomIdx));
            // Shuffle for random order
            Collections.shuffle(answerOptions);

            //We return the question
            Question toReturn = new ComparisonQuestion(firstActivity, answerOptions, answerOptions.indexOf(secondActivity)+1);
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

        try {
            // The consumption of the activity should be < 1000000, so we search for an activity with a consumption between
            // 0 and 999999 Wh. The reasons for this bound are that the user can more easily estimate "lower" consumptions
            // and that higher SI units cannot be used here, as they would make the slideBar difficult to configure.
            Activity a = activityDBController.getActivityExclAndInRange(List.of(),List.of(),0,999999);

            // Get the bounds for the input range for the estimation question. The consumption can now be safely cast
            // to an integer, as the above condition needs to be fulfilled.
            int[] bounds = utils.getBoundsEstimationQuestion(random, (int) a.consumption);

            // Create the question
            List<String> questionInfo = new ArrayList<>();
            questionInfo.add(Integer.toString(bounds[0]));
            questionInfo.add(Integer.toString(bounds[1]));
            Question toReturn = new EstimationQuestion(a, questionInfo);

            // Return/save the question
            questionDBController.add(toReturn);
            return toReturn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

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
                // Note that the cyclomatic complexity of this COULD be very bad. However, it is important to note
                // that it is very unlikely that questions are ever equal.
                if(questions.contains(generated)){
                    j--;
                    continue;
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
            // Note that the cyclomatic complexity of this COULD be very bad. However, it is important to note
            // that it is very unlikely that questions are ever equal.
            if(questions.contains(generated)){
                i--;
                continue;
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
                return ResponseEntity.ok(new AnswerResponseEntity(true, q.answer));
            } else {
                return ResponseEntity.ok(new AnswerResponseEntity(false, q.answer));
            }
        }

        if (q instanceof EstimationQuestion) {
            return ResponseEntity.ok(new AnswerResponseEntity((answer == q.answer), (answer - q.answer)));
        }

        return ResponseEntity.internalServerError().build();

    }
    */
}
