package server.database;

import commons.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityDB extends JpaRepository<Activity, String> {

    @Query(value = "SELECT * FROM activity Order By RAND() LIMIT 5",nativeQuery = true)
    public List<Activity> getFiveRandomActivities();

    @Query(value = "SELECT * FROM activity Order By RAND() LIMIT 2",nativeQuery = true)
    public List<Activity> getTwoRandomActivities();
}
