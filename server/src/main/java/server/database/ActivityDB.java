package server.database;

import commons.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityDB extends JpaRepository<Activity, String> {

    @Query(value = "SELECT * FROM activity Order By RAND() LIMIT 4",nativeQuery = true)
    public List<Activity> getFourRandomActivities();
}
