/*
https://www.baeldung.com/jackson-deserialization
 */

package server.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Activity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ActivityDBController {

    private final ActivityDB activityDB;

    private File jsonSource;

    /**
     * Creates a controller for the activity database
     *
     * @param activityDB database that will be used to store the activities
     */
    public ActivityDBController(ActivityDB activityDB) {

        this.activityDB = activityDB;
        this.jsonSource = null;

    }

    /**
     * This will set the default jsonSource to the activities.json file to allow for easier reloading
     */
    public void setJsonSourceToActivitiesFile() {

        File f = null;
        try {
            f = new File(Objects.requireNonNull(ActivityDBController.class.getClassLoader().getResource("activities/activities.json")).toURI());
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
        this.jsonSource = f;

    }

    /**
     * Reload the database with the same json file (delete everything and then update the database)
     */
    public void forceReload() {

        forceReload(jsonSource);

    }

    /**
     * Reload the database with a different json file (delete everything and then update the database)
     *
     * @param file a new json file with activities
     */
    public void forceReload(File file) {

        activityDB.deleteAll();

        update(file);

    }

    /**
     * Update the database with the same json file
     */
    public void update() {

        update(jsonSource);

    }

    /**
     * Update the database with activities from a new json file (try to read the file, then save every activity to the database)
     *
     * @param file a new json file with activities
     */
    public void update(File file) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            Activity[] activities = mapper.readValue(file, Activity[].class);

            for(Activity a : activities) {

                if(activityDB.findById(a.id).isPresent()) continue;

                activityDB.save(a);

            }

        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Print every activity from the database to the console
     */
    public void printAll() {

        activityDB.findAll().forEach(System.out::println);

    }

    /**
     * This method creates a list of activities from the activity database
     *
     * @return a List containing all activities
     */
    public List<Activity> listAll() {

        return activityDB.findAll();

    }

    /**
     * Method for returning the database
     *
     * @return the activity database
     */
    public ActivityDB getInternalDB() {

        return activityDB;

    }

    /**
     * Gets a list of 5 random activities from the database
     *
     * @return list of 5 random activities
     */
    public ArrayList<Activity> getFiveRandomActivities() {
        return activityDB.getFiveRandomActivities();
    }


    /**
     * Gets a list of 3 random activities from the database
     *
     * @return list of 3 random activities
     */
    public ArrayList<Activity> getThreeRandomActivities() {
        return activityDB.getThreeRandomActivities();
    }

    /**
     * Gets a random activity from the database
     *
     * @return a random activity
     */
    public Activity getRandomActivity() {
        return activityDB.getRandomActivity();
    }

    /**
     * Gets a random activity from the database which consumption is in a specified range, while excluding specific
     * activities and consumptions
     *
     * @param ids          The ids to exclude
     * @param consumptions Consumptions to exclude
     * @param lower        The lower bound (incl.)
     * @param upper        The upper bound (incl.)
     * @return a random activity which fulfills the above requirements
     */
    public Activity getActivityExclAndInRange(List<String> ids,
                                              List<Long> consumptions,
                                              long lower,
                                              long upper) {
        return activityDB.getActivityExclAndInRange(ids, consumptions, lower, upper);
    }

}
