package com.hcodesolutions.template.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConformPasswordDto implements Serializable {
    private String password;
    private String token;
}
