package com.hcodesolutions.template.controller;

import com.hcodesolutions.template.dto.PaginationDto;
import com.hcodesolutions.template.dto.ResponseDto;
import com.hcodesolutions.template.dto.RoleDto;
import com.hcodesolutions.template.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-21
 * @since 0.0.1
 */
@RestController
@RequestMapping("/role")
@CrossOrigin(origins = "*")
@Tag(name = "role controller", description = "role related operations...")
public class RoleController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseDto saveOrUpdateRole(@RequestBody RoleDto roleDto) {
        try {
            if (roleDto.getId() == null) {
                return roleService.saveRole(roleDto);
            } else {
                return roleService.updateRole(roleDto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/dis/{id}")
    public ResponseDto disableRole(@PathVariable Long id) {
        try {
            return roleService.disable(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/enb/{id}")
    public ResponseDto enableRole(@PathVariable Long id) {
        try {
            return roleService.enable(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/all")
    public ResponseDto getAllRoles(@RequestBody(required = false) PaginationDto paginationDto) {
        try {
            if (paginationDto != null) {
                return roleService.paginationRole(paginationDto);
            }else {
                return roleService.getAllRoles();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }
}
