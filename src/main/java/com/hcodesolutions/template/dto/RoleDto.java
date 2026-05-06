package com.hcodesolutions.template.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-20
 * @since 0.0.1
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto implements Serializable {
    private Long id;
    private String name;

    // map<menuId, permissionId>
    private Map<Long, List<Long>> roleMenuPermission;
}
