package com.hcodesolutions.template.dto;

import lombok.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDto {
    private Long id;
    private String password;
}
