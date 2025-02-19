package com.hcodesolutions.template.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-07
 * @since 0.0.1
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResDto implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String token;
    private List<String> role;
}
