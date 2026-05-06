package com.hcodesolutions.template.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class SelectByDto implements Serializable {
    private String selectedType;
    private String selectedValue;
}
