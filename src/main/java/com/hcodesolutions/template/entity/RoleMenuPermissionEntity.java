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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "role_menu_permission")
public class RoleMenuPermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_menu_permission_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_role_id", updatable = false)
    private MenuRoleEntity menuRole;

    @ManyToOne
    @JoinColumn(name = "permission_id", updatable = false)
    private PermissionEntity permission;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private boolean isActive;
}
