package server.database;

import commons.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Collection;


public interface ActivityDB extends JpaRepository<Activity, String> {

    /**
     * Uses query to generate 5 random activities from database
     *
     * @return List of 5 random activities from the database
     */
    @Query(value = "SELECT * FROM activity Order By RAND() LIMIT 5", nativeQuery = true)
    ArrayList<Activity> getFiveRandomActivities();


    /**
     * Uses query to generate 3 random activities from database
     *
     * @return List of 3 random activities from the database
     */
    @Query(value = "SELECT * FROM activity Order By RAND() LIMIT 3", nativeQuery = true)
    ArrayList<Activity> getThreeRandomActivities();

    /**
     * Uses query to generate one random activity from database
     *
     * @return A random activity from the database
     */
    @Query(value = "SELECT * FROM activity Order By RAND() LIMIT 1", nativeQuery = true)
    Activity getRandomActivity();

    /**
     * Uses query to generate one random activity from database, which consumption is in a specified range,
     * while excluding specific activities and consumptions
     *
     * @return A random activity from the database fulfilling the above-mentioned requirements
     */
    @Query(value = "SELECT * FROM ACTIVITY\n" +
            "WHERE CONSUMPTION BETWEEN :lower AND :upper\n" +
            "AND ID NOT IN :ids\n" +
            "AND CONSUMPTION NOT IN :consumptions\n" +
            "ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Activity getActivityExclAndInRange(@Param("ids") Collection<String> ids,
                                       @Param("consumptions") Collection<Long> consumptions,
                                       @Param("lower") long lower,
                                       @Param("upper") long upper);

}
