package server.database;

import commons.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionDB extends JpaRepository<Question, Long> {
}
