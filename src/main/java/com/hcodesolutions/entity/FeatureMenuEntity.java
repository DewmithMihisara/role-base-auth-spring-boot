package com.hcodesolutions.entity;

import jakarta.persistence.*;
import lombok.*;

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
@Table(name = "feature_menu")
public class FeatureMenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_menu_id")
    private Long id;

    @Column(name = "is_active")
    private boolean isActive;


    @ManyToOne
    @JoinColumn(name = "menu_id", updatable = false)
    private MenuEntity menu;

    @ManyToOne
    @JoinColumn(name = "feature_id", updatable = false)
    private FeatureEntity feature;
}
