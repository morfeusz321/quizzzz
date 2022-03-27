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
        if (page.hasContent() && page.getContent().get(0) != null) {
            Activity a = page.getContent().get(0);
            List<String> aw = new ArrayList<>();
            aw.add((long) ((utils.getRandomWithExclusion(random, 0.5, 2, 1) * a.consumption)) + " Wh");
            aw.add( a.consumption + " Wh");
            aw.add((long) (((utils.getRandomWithExclusion(random, 0.7, 2, 1) * a.consumption))) + " Wh");
            Collections.shuffle(aw);
            Question toReturn = new GeneralQuestion(a,aw,aw.indexOf(Long.toString(a.consumption)+" Wh") + 1);
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
            List<Activity> activities = new ArrayList<>();
            // Get first activity: No conditions.
            Activity first = activityDBController.getRandomActivity();
            if(first == null) {
                return null; // Something went wrong when trying to retrieve an activity.
            }
            activities.add(first);
            // Second activity: Bounds depend on first activity added. The id and consumption of the first activity are
            // excluded.
            long[] bounds = getLowerUpperBoundSmall(first.consumption);
            activities.add(activityDBController.getActivityExclAndInRange(
                    List.of(first.id),
                    List.of(first.consumption),
                    bounds[0],
                    bounds[1]
            ));
            if(activities.get(1) == null) {
                return getWhichIsMoreQuestion(); // The boundaries did not include a fitting activity. Try again.
            }
            // Third activity: Bounds depend on the average of the first and second activity. The ids and consumptions
            // of the previous activities are excluded.
            bounds = getLowerUpperBoundSmall((first.consumption + activities.get(1).consumption)/2);
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
     * Generates a (random) upper/lower bound for a given consumption, which is used to generate the new activities
     * with a close consumption to this one. The bound is dependent on the "scale" of the given consumption.
     * @param consumption the consumption from which to generate a range
     * @return an array with two longs, the lower bound (idx 0) and the upper bound (idx 1)
     */
    private long[] getLowerUpperBoundSmall(long consumption){
        // This is a method that creates a "small" range, that is closer to the initial value.
        // The range does not have to be generated randomly, as the activity itself is chosen randomly
        // within that range.
        // TODO: add method with bigger range, so that different "difficulties" can be generated
        if(consumption <= 1000){
            return new long[]{0, 10000};
        } else if(consumption <= 10000){
            return new long[]{1000,10000};
        } else if(consumption <= 100000){
            return new long[]{10000,10000000L};
        } else if(consumption <= 10000000L){
            return new long[]{100000,1000000000L};
        } else if(consumption <= 1000000000L){
            return new long[]{10000000L,100000000000L};
        } else if(consumption <= 100000000000L){
            return new long[]{1000000000L,100000000000L};
        } else {
            return new long[]{100000000000L,Long.MAX_VALUE};
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

        ActivityDB activityDB = activityDBController.getInternalDB();

        long count = activityDB.count();
        int index;
        try {
            index = random.nextInt((int) count);
        } catch (IllegalArgumentException e) {
            return null;
        }

        Page<Activity> page = activityDB.findAll(PageRequest.of(index, 1));
        if (page.hasContent() && page.getContent().get(0) != null) {
            Activity a = page.getContent().get(0);
            List<String> aw = new ArrayList<>();

            long min = a.consumption - 100;
            long max = a.consumption + 100;

            int shift = random.nextInt(200) - 100;

            min = min + shift;
            max = max + shift;

            if(min < 0) {
                max = max - min;
                min = 0;
            }

            aw.add(Long.toString(min));
            aw.add(Long.toString(max));
            aw.add(Long.toString(a.consumption));
            Question toReturn = new EstimationQuestion(a, aw);
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
