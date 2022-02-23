/*
https://www.baeldung.com/jackson-deserialization
 */

package server.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import commons.Activity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ActivityDBController {

    private final ActivityDB activityDB;

    public ActivityDBController(ActivityDB activityDB) {

        this.activityDB = activityDB;

    }

    public void forceReload(File file) {

        activityDB.deleteAll();

        update(file);

    }

    public void update(File file) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Activity.class, new FromSourceActivityDeserializer());
            mapper.registerModule(module);

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

}
