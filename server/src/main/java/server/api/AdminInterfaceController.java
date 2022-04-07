package server.api;

import commons.Activity;
import commons.CommonUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ActivityDB;
import server.database.ActivityDBController;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/debug")
public class AdminInterfaceController {

    private final ActivityDBController activityDBController;

    /**
     * Creates an admin interface with an activityDBController to retrieve the activities and images.
     * This admin interface is used for debugging purposes, or editing/adding/deleting activities.
     *
     * @param activityDBController An activityDBController to retrieve the activities and images
     */
    public AdminInterfaceController(ActivityDBController activityDBController) {

        this.activityDBController = activityDBController;

    }

    /**
     * Get all activities from the database
     *
     * @return a list of activities
     */
    @GetMapping("/activities")
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(activityDBController.listAll());
    }

    /**
     * Saves a modified activity to the database
     *
     * @param activity a modified activity
     * @return 200 OK: Activity saved, 400 Bad Request: Wrong input
     */
    @PostMapping("/activities/edit")
    public ResponseEntity<Activity> edit(@RequestBody Activity activity) {

        if(activity.id == null || CommonUtils.isNullOrEmpty(activity.imagePath) || CommonUtils.isNullOrEmpty(activity.title)
                || activity.consumption == 0) {
            return ResponseEntity.badRequest().build();
        }
        ActivityDB activityDB = activityDBController.getInternalDB();
        Activity saved = activityDB.save(activity);
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes an activity from the database
     *
     * @param activity the activity that will be deleted
     * @return 200 OK: Activity deleted, 400 Bad Request: Wrong input
     */
    @PostMapping("/activities/delete")
    public ResponseEntity<Activity> delete(@RequestBody Activity activity) {

        if(activity.id == null) {
            return ResponseEntity.badRequest().build();
        }
        ActivityDB activityDB = activityDBController.getInternalDB();
        activityDB.delete(activity);
        return ResponseEntity.ok(activity);
    }

    /**
     * Imports a new list of activities to the database and overrides (deletes) the current DB
     *
     * @param path path to the json file
     * @return 200 OK: List imported, 400 Bad Request: Wrong input
     */
    @PostMapping("/activities/import")
    public ResponseEntity<String> importActivity(@RequestBody String path) {

        if(CommonUtils.isNullOrEmpty(path)) {
            return ResponseEntity.badRequest().build();
        }
        File file = new File(path);
        activityDBController.forceReload(file);
        return ResponseEntity.ok(path);

    }

    /**
     * Imports a new list of activities to the database without overriding the current DB
     *
     * @param path path to the json file
     * @return 200 OK: List imported, 400 Bad Request: Wrong input
     */
    @PostMapping("/activities/importupdate")
    public ResponseEntity<String> importActivityUpdate(@RequestBody String path) {

        if(CommonUtils.isNullOrEmpty(path)) {
            return ResponseEntity.badRequest().build();
        }
        File file = new File(path);
        activityDBController.update(file);
        return ResponseEntity.ok(path);

    }

}