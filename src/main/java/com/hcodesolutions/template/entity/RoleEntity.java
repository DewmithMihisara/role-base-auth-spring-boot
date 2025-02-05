package com.hcodesolutions.template.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;


    @CreationTimestamp
    @Column(name = "create_date", updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    @Column(name = "create_by")
    private String createBy;

    @UpdateTimestamp
    @Column(name = "modify_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifyDate;

    @Column(name = "modify_by")
    private String modifyBy;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private boolean isActive;

    @JsonIgnore
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = MenuRoleEntity.class)
    List<MenuRoleEntity> menuRoleEntities;

    @JsonIgnore
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = UserRoleEntity.class)
    List<UserRoleEntity> userRoleEntities;
}
