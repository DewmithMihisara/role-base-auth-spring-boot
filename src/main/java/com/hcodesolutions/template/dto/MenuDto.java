package com.hcodesolutions.template.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-20
 * @since 0.0.1
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuDto implements Serializable {
    private Long id;
    private String description;
    private String effectiveDate;
    @NotEmpty(message = "name is required")
    private String name;
    @NotEmpty(message = "display order is required")
    private Integer displayOrder;
    private Long parentMenu;
    @NotEmpty(message = "rout is required")
    private String route;
    private String icon;
    private String createBy;
    private String modifyBy;
    private Boolean isActive;
    @NotEmpty(message = "permission(s) is required")
    private List<Long> permissionIds;
}
