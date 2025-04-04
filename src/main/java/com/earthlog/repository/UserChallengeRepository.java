package com.earthlog.repository;

import com.earthlog.entity.UserChallenge;
import com.earthlog.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {

    List<UserChallenge> findByUserId(Long userId);

    List<UserChallenge> findByUserIdAndStatus(Long userId, ChallengeStatus status);

    Optional<UserChallenge> findByUserIdAndChallengeId(Long userId, Long challengeId);

    boolean existsByUserIdAndChallengeId(Long userId, Long challengeId);
}
