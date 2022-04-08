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
            // First retrieve a random activity from the database.
            Activity a = activityDBController.getRandomActivity();
            if(a == null) {
                return null; // Something went wrong when trying to retrieve an activity.
            }

            String mainConsumptionString = utils.createConsumptionString(a.consumption);
            long[] consumptions = new long[2];
            // Use "smart" bound generation
            long[] bounds = utils.getBoundsGeneralQuestion(a.consumption);

            // Generate the first answer option, while excluding the real answer and a range around it.
            // Check if a number can be generated. If the probability of randomly selecting a valid number is under 30%,
            // a new general question should be generated, as otherwise StackOverflow errors might be thrown.
            if(!utils.checkIfGeneratable(List.of(a.consumption), 0.1, bounds[0], bounds[1])){
                // Might generate StackOverFlowErrors, try again
                return getGeneralQuestion();
            }
            // Generate the consumption, while taking the conditions (bounds, exclusion...) into account.
            consumptions[0] = utils.randomLongInRangeExcl(
                    bounds[0],
                    bounds[1],
                    random,
                    List.of(mainConsumptionString),
                    List.of(a.consumption),
                    0.1
            );
            String secondConsumptionString = utils.createConsumptionString(consumptions[0]);

            // Generate the second answer option, while excluding the previous answer options and a ranges around them.
            // Check if a number can be generated. If the probability of randomly selecting a valid number is under 30%,
            // a new general question should be generated, as otherwise StackOverflow errors might be thrown.
            if(!utils.checkIfGeneratable(List.of(a.consumption, consumptions[0]), 0.1, bounds[0], bounds[1])){
                // Might generate StackOverFlowErrors, try again
                return getGeneralQuestion();
            }
            // Generate the consumption, while taking the conditions (bounds, exclusion...) into account.
            consumptions[1] = utils.randomLongInRangeExcl(
                    bounds[0],
                    bounds[1],
                    random,
                    List.of(mainConsumptionString, secondConsumptionString),
                    List.of(a.consumption, consumptions[0]),
                    0.1
            );

            // Create the answer option list using the SI strings.
            List<String> aw = new ArrayList<>();
            aw.add(mainConsumptionString);
            aw.add(secondConsumptionString);
            aw.add(utils.createConsumptionString(consumptions[1]));
            Collections.shuffle(aw);

            // Return and save generated question.
            Question toReturn = new GeneralQuestion(a, aw, aw.indexOf(mainConsumptionString) + 1);
            questionDBController.add(toReturn);
            return toReturn;
        } catch (StackOverflowError e) {
            // StackOverflowError: No good number could be found in the generated range, many times in a row.
            // This should not be the case, as the safety check should prohibit this. So, if this is the case,
            // it is because no question can be generated from the database (at all).
            System.out.println("Error: No valid question could be generated from the database.");
            return null;
        } catch (Exception e) {
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

            // First we retrieve a random activity -> main activity
            Activity main = activityDBController.getRandomActivity();

            if(main == null) {
                return null; // Something went wrong
            }

            // Get a second activity which is in a small range, i.e. 5%, around the actual one
            Activity answer = activityDBController.getActivityExclAndInRange(
                    List.of(main.id), List.of(), // no values need to be excluded
                    Math.round(main.consumption - main.consumption * 0.05),
                    Math.round(main.consumption + main.consumption * 0.05)
            );

            if(answer == null) {
                // We can only find activities with a difference bigger than 5% of the original activity, we
                // need to search again. First we try to find one with a "higher" range, of 10%:
                answer = activityDBController.getActivityExclAndInRange(
                        List.of(main.id), List.of(), // no values need to be excluded
                        Math.round(main.consumption - main.consumption * 0.1),
                        Math.round(main.consumption + main.consumption * 0.1)
                );
                if(answer == null){
                    // No answer could be generated, try to find another activity
                    return getComparisonQuestion();
                }
            }

            List<Activity> chosenActivities = getAnswerOptionsComparisonQuestion(main, answer);
            if(chosenActivities == null) {
                // If it does not have any elements (or less than 2), no/not enough fitting activities could be
                // found, so try again.
                return getComparisonQuestion();
            }

            // Create answer option list and add the actual answer.
            List<Activity> answerOptions = new ArrayList<>();
            answerOptions.add(answer);

            // Now we can get 2 random activities from the generated list, and add those as answer options.
            int idx = random.nextInt(chosenActivities.size());
            answerOptions.add(chosenActivities.remove(idx));
            idx = random.nextInt(chosenActivities.size());
            answerOptions.add(chosenActivities.remove(idx));

            // Shuffle for random order
            Collections.shuffle(answerOptions);

            //We return the question
            Question toReturn = new ComparisonQuestion(main, answerOptions, answerOptions.indexOf(answer)+1);
            questionDBController.add(toReturn);
            return toReturn;

        } catch (StackOverflowError e){
            System.out.println("Error: No valid question could be generated from the database.");
            return null;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a list of activities that can be used as answer options, or null if less than 2 could be generated.
     * @param main The main activity of the comparison question, i.e. the title
     * @param answer The answer of the comparison question
     * @return a list of activities that can be used as answer options, or null if less than 2 could be generated.
     */
    public List<Activity> getAnswerOptionsComparisonQuestion(Activity main, Activity answer) {

        List<Activity> chosenActivities = new ArrayList<>();
        // Now two new activities are needed: Get 2 that are 20-40% below the answer, and 2 that are 20-40% above.
        // Those are put into a list, null values are filtered out.

        List<String> exclIds = new ArrayList<>(List.of(main.id, answer.id)); // use this so that it is not immutable
        List<Long> exclConsumptions = List.of(main.consumption, answer.consumption);
        // Get 2 activities in the "lower" part, i.e. 20-40% below the answer
        // Use the answer for the bound calculation, as we want to distinguish the answer options
        long lowerBound = (long) (answer.consumption * 0.6);
        long upperBound = (long) (answer.consumption * 0.8);
        for(int i = 0; i < 4; i++){
            Activity chosen = activityDBController.getActivityExclAndInRange(
                    exclIds, exclConsumptions, lowerBound, upperBound // exclude main and answer consumption
            );
            if(chosen != null) {
                chosenActivities.add(chosen);
                exclIds.add(chosen.id);
            }
            if(i == 1) {
                // Use other bounds, now to get 2 activities in the "upper" part, i.e. 20-40% above the answer
                lowerBound = (long) (answer.consumption * 1.2);
                upperBound = (long) (answer.consumption * 1.4);
            }
        }

        // Not enough activities could be found
        if(chosenActivities.size() < 2) {
            return null;
        }
        // Otherwise, return the generated activities
        return chosenActivities;

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

}
