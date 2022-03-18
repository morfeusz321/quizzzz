/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;

import commons.Score;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;

import server.database.ActivityDBController;
import server.database.QuestionDBController;
import server.database.ScoreDBController;

@SpringBootApplication
@EntityScan(basePackages = { "commons", "server" })
public class Main {
    /**
     * Main class used to start the server of our application
     * @param args arguments passed to main before starting the application
     */
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        //context.getBean(ActivityDBController.class).setJsonSourceToActivitiesFile();
        //context.getBean(ActivityDBController.class).forceReload();
        context.getBean(QuestionDBController.class).clear();

        ScoreDBController sdb = context.getBean(ScoreDBController.class);
        sdb.clear();
        sdb.add(new Score("Gijs", 400));
        sdb.add(new Score("Some guy", 200));
        sdb.add(new Score("Pro Gamer", 600));
        sdb.add(new Score("Not pro gamer", 100));
        sdb.add(new Score("YEah", 500));
        sdb.add(new Score("asdasd", 130));
        sdb.add(new Score("aasasdfdsgf", 130));
        sdb.add(new Score("jfaodsiupq", 225));
        sdb.add(new Score("qpweoiiop", 315));
        sdb.add(new Score("qweuuurpoi", 758));
        sdb.add(new Score("qquweoiiouo", 190));


    }
}