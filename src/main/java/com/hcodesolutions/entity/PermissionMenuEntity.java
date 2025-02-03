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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "permission_menu")
public class PermissionMenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_menu_id")
    private Long id;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "menu_id", updatable = false)
    private MenuEntity menu;

    @ManyToOne
    @JoinColumn(name = "permission_id", updatable = false)
    private PermissionEntity permission;
}
