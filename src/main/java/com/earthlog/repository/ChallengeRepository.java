package com.earthlog.repository;

import com.earthlog.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByIsActiveTrue();

    List<Challenge> findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        LocalDate startDate, LocalDate endDate
    );
}
