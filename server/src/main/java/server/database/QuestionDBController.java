package server.database;

import commons.Question;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class QuestionDBController {

    private final QuestionDB questionDB;

    /**
     * Creates a controller for the question database
     * @param questionDB the question database to be used by this controller
     */
    public QuestionDBController(QuestionDB questionDB) {

        this.questionDB = questionDB;

    }

    /**
     * Deletes all entries in the question database
     */
    public void clear() {

        questionDB.deleteAll();

    }

    /**
     * Retrieves a question from the database from its unique ID
     * @param uuid the unique id of the question to retrieve
     * @return the question if found, null otherwise
     */
    public Question getById(UUID uuid) {

        return questionDB.findById(uuid).orElse(null);

    }

    /**
     * Deletes a question from the database by its unique ID
     * @param uuid the unique ID of the question to delete
     */
    public void deleteById(UUID uuid) {

        questionDB.deleteById(uuid);

    }

    /**
     * Adds a question to the database
     * @param question the question to add to the database
     */
    public void add(Question question) {

        questionDB.save(question);

    }

}
