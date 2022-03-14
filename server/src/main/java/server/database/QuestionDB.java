package server.database;

import commons.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionDB extends JpaRepository<Question, UUID> {
}
