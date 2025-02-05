package com.hcodesolutions.template.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-03
 * @since 0.0.1
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_menu")
public class UserMenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_menu_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "permission_id", updatable = false)
    private PermissionEntity permission;

    @ManyToOne
    @JoinColumn(name = "menu_id", updatable = false)
    private MenuEntity menu;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private boolean isActive;
}
