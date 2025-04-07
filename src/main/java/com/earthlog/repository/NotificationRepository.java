package com.earthlog.repository;

import com.earthlog.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type " +
           "AND n.createdAt > :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentByUserIdAndType(
            @Param("userId") Long userId,
            @Param("type") Notification.NotificationType type,
            @Param("since") LocalDateTime since
    );

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.user.id = :userId " +
           "AND n.type = :type AND n.referenceId = :referenceId AND n.createdAt > :since")
    boolean existsRecentNotification(
            @Param("userId") Long userId,
            @Param("type") Notification.NotificationType type,
            @Param("referenceId") Long referenceId,
            @Param("since") LocalDateTime since
    );
}
