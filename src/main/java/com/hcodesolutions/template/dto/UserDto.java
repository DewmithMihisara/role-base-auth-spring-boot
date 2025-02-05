package com.hcodesolutions.template.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-05
 * @since 0.0.1
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String email;
    private String contactNumber;
    private Integer tryCount;
    private Boolean isLocked;
    private Long companyId;
    private String createBy;
    private String modifyBy;
    private Boolean isActive;
    private List<Long> roleIds;
}
