package server.game;

import commons.Activity;
import server.database.ActivityDBController;

/**
 * General utility concerning the initialization of test objects for the Game tests.
 */
public class GameTestUtils {

    /**
     * Initializes the database of the given ActivityDBController with a specific set of activities.
     *
     * @param activityDBController The activityDBController for the test activity database
     */
    public void initActivityDB(ActivityDBController activityDBController) {
        activityDBController.getInternalDB().save(new Activity("id1", "imagePath", "1", 1));
        activityDBController.getInternalDB().save(new Activity("id2", "imagePath", "2", 23));
        activityDBController.getInternalDB().save(new Activity("id3", "imagePath", "3", 332));
        activityDBController.getInternalDB().save(new Activity("id4", "imagePath", "4", 4));
        activityDBController.getInternalDB().save(new Activity("id5", "imagePath", "5", 534));
        activityDBController.getInternalDB().save(new Activity("id6", "imagePath", "6", 61));
        activityDBController.getInternalDB().save(new Activity("id7", "imagePath", "7", 71));
        activityDBController.getInternalDB().save(new Activity("id8", "imagePath", "8", 423));
        activityDBController.getInternalDB().save(new Activity("id9", "imagePath", "9", 424));
        activityDBController.getInternalDB().save(new Activity("id10", "imagePath", "10", 10));
        activityDBController.getInternalDB().save(new Activity("id11", "imagePath", "11", 99));
        activityDBController.getInternalDB().save(new Activity("id12", "imagePath", "12", 33));
        activityDBController.getInternalDB().save(new Activity("id13", "imagePath", "13", 22));
        activityDBController.getInternalDB().save(new Activity("id14", "imagePath", "14", 120));
        activityDBController.getInternalDB().save(new Activity("id15", "imagePath", "15", 234));
        activityDBController.getInternalDB().save(new Activity("id16", "imagePath", "16", 4567));
        activityDBController.getInternalDB().save(new Activity("id17", "imagePath", "17", 211));
        activityDBController.getInternalDB().save(new Activity("id18", "imagePath", "18", 2244));
        activityDBController.getInternalDB().save(new Activity("id19", "imagePath", "19", 34));
        activityDBController.getInternalDB().save(new Activity("id20", "imagePath", "20", 320));

    }

}
