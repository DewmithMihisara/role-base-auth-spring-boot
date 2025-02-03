package com.hcodesolutions.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-02
 * @since 0.0.1
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "plan_feature")
public class PlanFeatureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_feature_id")
    private Long id;

    @Column(name = "effective_date")
    @Temporal(TemporalType.DATE)
    private Date effectiveDate;


    @ManyToOne
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlanEntity plan;

    @ManyToOne
    @JoinColumn(name = "feature_id")
    private FeatureEntity feature;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private boolean isActive;

}
