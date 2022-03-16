package server.database;

import commons.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreDB extends JpaRepository<Score, String> {
    // TODO: we might decide later on that using the Player class makes more sense here,
    //  I decided to go for String for now, because we do not really need other attributes
    //  than the name here
}

