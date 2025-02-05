package com.hcodesolutions.template.dto;

import lombok.*;

import java.io.Serializable;
import java.util.HashMap;

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
public class ResponseDto implements Serializable {
    private String message;
    private Integer status;
    private HashMap<String, Object> data = new HashMap<>();

    public ResponseDto(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
