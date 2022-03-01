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
import java.util.List;

@Component
public class ActivityDBController {

    private final ActivityDB activityDB;

    private final File jsonSource;

    public ActivityDBController(ActivityDB activityDB) {

        this.activityDB = activityDB;

        File f = null;
        try {
            f = new File(ActivityDBController.class.getClassLoader().getResource("activities.json").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.jsonSource = f;

    }

    public void forceReload() {

        forceReload(jsonSource);

    }

    public void forceReload(File file) {

        activityDB.deleteAll();

        update(file);

    }

    public void update() {

        update(jsonSource);

    }

    public void update(File file) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            Activity[] activities = mapper.readValue(file, Activity[].class);

            for(Activity a : activities) {

                if(activityDB.findById(a.id).isPresent()) continue;

                activityDB.save(a);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void printAll() {

        activityDB.findAll().forEach(System.out::println);

    }

    public List<Activity> listAll() {

        return activityDB.findAll();

    }

    public ActivityDB getInternalDB() {

        return activityDB;

    }

}
