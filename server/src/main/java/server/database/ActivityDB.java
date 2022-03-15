package server.database;

import commons.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityDB extends JpaRepository<Activity, String> {

    /**
     * Uses query to generate 5 random activities from database
     * @return List of 5 random activities from the database
     */
    @Query(value = "SELECT * FROM activity Order By RAND() LIMIT 5",nativeQuery = true)
    public List<Activity> getFiveRandomActivities();


    /**
     * Uses query to generate 2 random activities from database
     * @return List of 2 random activities from the database
     */
    @Query(value = "SELECT * FROM activity Order By RAND() LIMIT 3",nativeQuery = true)
    public List<Activity> getThreeRandomActivities();
}
