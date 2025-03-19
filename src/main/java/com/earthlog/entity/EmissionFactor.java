package com.earthlog.entity;

import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "emission_factors", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"category", "activity_type", "region"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 50)
    private ActivityType activityType;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal factor;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(length = 10)
    @Builder.Default
    private String region = "GLOBAL";

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String source;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
