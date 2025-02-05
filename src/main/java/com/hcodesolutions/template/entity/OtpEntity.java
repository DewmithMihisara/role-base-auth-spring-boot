package com.hcodesolutions.template.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-02
 * @since 0.0.1
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "otp")
public class OtpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "otp_no")
    private String otp;

    @Column(name = "validity")
    private int validity;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "branch_id")
    private Long branchId;


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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
