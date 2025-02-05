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
@Table(name = "user_role")
public class UserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "role_id", updatable = false)
    private RoleEntity role;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private boolean isActive;
}
