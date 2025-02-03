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
@Builder
@Entity
@Table(name = "menu_role")
public class MenuRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_role_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_id", updatable = false)
    private MenuEntity menu;

    @ManyToOne
    @JoinColumn(name = "role_id", updatable = false)
    private RoleEntity role;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private boolean isActive;
}
