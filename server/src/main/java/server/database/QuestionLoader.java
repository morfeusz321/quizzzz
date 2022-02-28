package server.database;

import commons.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionLoader {

    private final QuestionDB questionDB;

    public QuestionLoader(QuestionDB questionDB) {

        this.questionDB = questionDB;

    }

    public void initQuestionDB() {

        questionDB.deleteAll();

        Question sampleQuestion1 = new Question("What is 2 + 2?");
        Question sampleQuestion2 = new Question("Is this a question?");
        Question sampleQuestion3 = new Question("What is the first letter of this question?");
        Question sampleQuestion4 = new Question("What is the derivative with respect to x of x?");

        questionDB.save(sampleQuestion1);
        questionDB.save(sampleQuestion2);
        questionDB.save(sampleQuestion3);
        questionDB.save(sampleQuestion4);


    }

}
