package server;

import commons.Activity;
import commons.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import server.database.ActivityDB;
import server.database.ActivityDBController;

import java.util.List;

@Controller
@RequestMapping("/")
public class WebInterface {

    private ActivityDBController activityDBController;

    /**
     * Creates a web interface with an activityDBController to retrieve the images. This web interface is
     * used for debugging purposes.
     * @param activityDBController An activityDBController to retrieve the images for the activities
     */
    public WebInterface(ActivityDBController activityDBController) {

        this.activityDBController = activityDBController;

    }

    /**
     * Converts a list to HTML (as a string)
     * @param list The list to be converted
     * @param <T> The type of the elements of the list
     * @return Returns the given list as a string of HTML
     */
    private <T> String listToHTML(List<T> list) {

        StringBuilder sb = new StringBuilder();

        sb.append("<p>");
        list.forEach(item -> sb.append("<br>").append(item));
        sb.append("</p>");

        return sb.toString();

    }

    /**
     * Returns the main page (index page)
     * @return The filename of the main page
     */
    @GetMapping("/")
    public String index() {

        return "index.html";

    }

    /**
     * Returns the page which lists all current activities that are in the database at the moment
     * @return HTML of the page which lists all current activities that are in the database
     */
    @GetMapping("/debug/listactivities")
    public ResponseEntity<String> debugListActivities() {

        StringBuilder sb = new StringBuilder();

        sb.append("<html>" +
                "<head>" +
                "<title>Debug - List Activities</title>" +
                "</head>" +
                "<body>" +
                "<a href=\"../../\"><button>Back</button></a>");
        sb.append(listToHTML(activityDBController.listAll()));
        sb.append("</body></html>");

        return ResponseEntity.ok(sb.toString());

    }

    /**
     * Get all activities from the database
     * @return a list of activities
     */
    @GetMapping("/debug/activities")
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(activityDBController.listAll());
    }

    /**
     * Saves a modified activity to the database
     * @param activity a modified activity
     * @return 200 OK: Activity saved, 400 Bad Request: Wrong input
     */
    @PostMapping("/debug/activities/edit")
    public ResponseEntity<Activity> edit(@RequestBody Activity activity) {

        if (activity.id == null || Utils.isNullOrEmpty(activity.imagePath) || Utils.isNullOrEmpty(activity.title)
                || activity.consumption == 0) {
            return ResponseEntity.badRequest().build();
        }
        ActivityDB activityDB = activityDBController.getInternalDB();
        Activity saved = activityDB.save(activity);
        return ResponseEntity.ok(saved);
    }

}