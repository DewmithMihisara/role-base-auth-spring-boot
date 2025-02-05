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
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permission")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    @Column(name = "name")
    private String name;


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
    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = UserMenuEntity.class)
    List<UserMenuEntity> userMenuEntities;

    @JsonIgnore
    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = RoleMenuPermissionEntity.class)
    List<RoleMenuPermissionEntity> roleMenuPermissionEntities;

    @JsonIgnore
    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = PermissionMenuEntity.class)
    List<PermissionMenuEntity> permissionMenuEntities;
}
