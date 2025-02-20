package com.hcodesolutions.template.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-21
 * @since 0.0.1
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MenuLoadingDto implements Serializable {
    private int order;
    private String name;
    private String icon;
    private String route;
    List<MenuLoadingDto> children;
}
