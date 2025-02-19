package com.hcodesolutions.template.dto;

import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "first name is required")
    private String firstName;
    private String lastName;
    private String userName;
    @NotEmpty(message = "email is required")
    private String email;
    @NotEmpty(message = "no is required")
    private String contactNumber;
    private Integer tryCount;
    private Boolean isLocked;
    private String createBy;
    private String modifyBy;
    private Boolean isActive;
    @NotEmpty(message = "role is required")
    private List<Long> roleIds;

    // if password set via emails, remove this
    private String password;
}
