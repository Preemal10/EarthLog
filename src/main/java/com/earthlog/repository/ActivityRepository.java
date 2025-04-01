package com.earthlog.repository;

import com.earthlog.entity.Activity;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findByUserId(Long userId, Pageable pageable);

    Page<Activity> findByUserIdAndCategory(Long userId, ActivityCategory category, Pageable pageable);

    List<Activity> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Page<Activity> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT SUM(a.calculatedCo2) FROM Activity a WHERE a.user.id = :userId AND a.date BETWEEN :startDate AND :endDate")
    BigDecimal sumCo2ByUserIdAndDateBetween(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT a.category, SUM(a.calculatedCo2) FROM Activity a WHERE a.user.id = :userId AND a.date BETWEEN :startDate AND :endDate GROUP BY a.category")
    List<Object[]> sumCo2ByUserIdAndDateBetweenGroupByCategory(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT a.date, SUM(a.calculatedCo2) FROM Activity a WHERE a.user.id = :userId AND a.date BETWEEN :startDate AND :endDate GROUP BY a.date ORDER BY a.date")
    List<Object[]> sumCo2ByUserIdAndDateBetweenGroupByDate(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.user.id = :userId AND a.date BETWEEN :startDate AND :endDate")
    Long countByUserIdAndDateBetween(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT AVG(daily.total) FROM (SELECT SUM(a.calculatedCo2) as total FROM Activity a WHERE a.date BETWEEN :startDate AND :endDate GROUP BY a.user.id, a.date) daily")
    BigDecimal findAverageDailyCo2(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Count activities by user
    long countByUserId(Long userId);

    // Count activities by user and type
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.user.id = :userId AND a.activityType = :activityType")
    int countByUserIdAndActivityType(@Param("userId") Long userId, @Param("activityType") ActivityType activityType);

    // Sum emissions for goal progress check
    @Query("SELECT COALESCE(SUM(a.calculatedCo2), 0) FROM Activity a WHERE a.user.id = :userId AND a.date BETWEEN :startDate AND :endDate")
    BigDecimal sumEmissionsByUserAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Count activities by category for a user
    @Query("SELECT a.category, COUNT(a) FROM Activity a WHERE a.user.id = :userId GROUP BY a.category")
    List<Object[]> countByUserIdGroupByCategory(@Param("userId") Long userId);
}
