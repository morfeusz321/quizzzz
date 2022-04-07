package server.database;

import commons.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreDB extends JpaRepository<Score, String> {
}

