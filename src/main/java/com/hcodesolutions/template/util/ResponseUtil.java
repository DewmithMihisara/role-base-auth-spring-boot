package com.hcodesolutions.template.util;

import lombok.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-07
 * @since 0.0.1
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResponseUtil<T> {
    private int code;
    private String message;
    private T data;

    public ResponseUtil(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
