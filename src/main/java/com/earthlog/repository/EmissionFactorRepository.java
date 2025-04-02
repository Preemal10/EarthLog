package com.earthlog.repository;

import com.earthlog.entity.EmissionFactor;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long> {

    Optional<EmissionFactor> findByActivityTypeAndRegion(ActivityType activityType, String region);

    @Query("SELECT ef FROM EmissionFactor ef WHERE ef.activityType = :activityType AND (ef.region = :region OR ef.region = 'GLOBAL') ORDER BY CASE WHEN ef.region = :region THEN 0 ELSE 1 END")
    List<EmissionFactor> findByActivityTypeAndRegionWithFallback(
        @Param("activityType") ActivityType activityType,
        @Param("region") String region
    );

    List<EmissionFactor> findByCategory(ActivityCategory category);

    List<EmissionFactor> findByRegion(String region);

    boolean existsByActivityTypeAndRegion(ActivityType activityType, String region);
}
