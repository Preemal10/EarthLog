package com.earthlog.repository;

import com.earthlog.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserId(Long userId);

    List<Goal> findByUserIdAndIsActiveTrue(Long userId);

    List<Goal> findByUserIdAndEndDateGreaterThanEqual(Long userId, LocalDate date);

    List<Goal> findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        Long userId, LocalDate endDate, LocalDate startDate
    );
}
