package server;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import server.database.ActivityDBController;

import java.util.List;

@Controller
@RequestMapping("/")
public class WebInterface {

    private ActivityDBController activityDBController;

    public WebInterface(ActivityDBController activityDBController) {

        this.activityDBController = activityDBController;

    }

    private <T> String listToHTML(List<T> list) {

        StringBuilder sb = new StringBuilder();

        sb.append("<p>");
        list.forEach(item -> sb.append("<br>").append(item));
        sb.append("</p>");

        return sb.toString();

    }

    @GetMapping("/")
    public String index() {

        return "index.html";

    }

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

}