package com.hcodesolutions.template.service;

import com.hcodesolutions.template.dto.PaginationDto;
import com.hcodesolutions.template.dto.ResponseDto;
import com.hcodesolutions.template.dto.RoleDto;
import com.hcodesolutions.template.entity.MenuEntity;
import com.hcodesolutions.template.entity.MenuRoleEntity;
import com.hcodesolutions.template.entity.RoleEntity;
import com.hcodesolutions.template.entity.RoleMenuPermissionEntity;
import com.hcodesolutions.template.repository.*;
import com.hcodesolutions.template.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-20
 * @since 0.0.1
 */
@Service
public class RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final MenuRoleRepository menuRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMenuPermissionRepository roleMenuPermissionRepository;

    public RoleService(RoleRepository roleRepository, MenuRepository menuRepository, MenuRoleRepository menuRoleRepository, PermissionRepository permissionRepository, RoleMenuPermissionRepository roleMenuPermissionRepository) {
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
        this.menuRoleRepository = menuRoleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMenuPermissionRepository = roleMenuPermissionRepository;
    }

    public ResponseDto saveRole(RoleDto roleDto){
        try {
            roleRepository.findByRoleName(roleDto.getName()).ifPresent(role -> {
                throw new RuntimeException("Role already exists");
            });

            RoleEntity roleEntity = RoleEntity.builder()
                    .roleName(roleDto.getName().toUpperCase())
                    .isActive(true)
                    .createBy(CommonUtils.getUser().getUsername())
                    .build();

            List<MenuRoleEntity> menuRoleEntities = new ArrayList<>();
            List<RoleMenuPermissionEntity> roleMenuPermissionEntities = new ArrayList<>();

            roleDto.getRoleMenuPermission().forEach((menuId, permissionIds) -> {
                menuRepository.findById(menuId).ifPresent(menuEntity -> {
                    MenuRoleEntity menuRoleEntity = MenuRoleEntity.builder()
                            .role(roleEntity)
                            .menu(menuEntity)
                            .build();
                    menuRoleEntities.add(menuRoleEntity);

                    permissionIds.forEach(permissionId -> {
                        permissionRepository.findById(permissionId).ifPresent(permissionEntity -> {
                            RoleMenuPermissionEntity roleMenuPermissionEntity = RoleMenuPermissionEntity.builder()
                                    .menuRole(menuRoleEntity)
                                    .permission(permissionEntity)
                                    .isActive(true)
                                    .build();
                            roleMenuPermissionEntities.add(roleMenuPermissionEntity);
                        });
                    });
                });
            });

            if (roleRepository.save(roleEntity) != null && menuRoleRepository.saveAll(menuRoleEntities) != null && roleMenuPermissionRepository.saveAll(roleMenuPermissionEntities) != null){
                logger.info("Role saved successfully");
                return new ResponseDto("Role saved successfully", 200);
            }else {
                logger.error("Failed to save role");
                return new ResponseDto("Failed to save role", 500);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    public ResponseDto updateRole(RoleDto roleDto){
        try {
            RoleEntity roleEntity = roleRepository.findById(roleDto.getId()).orElseThrow(() -> new RuntimeException("Role not found"));

            roleEntity.setRoleName(roleDto.getName().toUpperCase());
            roleEntity.setModifyBy(CommonUtils.getUser().getUsername());

            List<MenuRoleEntity> menuRoleEntities = new ArrayList<>(menuRoleRepository.findById(roleEntity.getId()).stream().toList());
            List<RoleMenuPermissionEntity> roleMenuPermissionEntities = new ArrayList<>(roleMenuPermissionRepository.findById(roleEntity.getId()).stream().toList());

            menuRoleEntities.forEach(menuRoleEntity -> {
                menuRoleEntity.setActive(false);
            });

            roleMenuPermissionEntities.forEach(roleMenuPermissionEntity -> {
                roleMenuPermissionEntity.setActive(false);
            });

            roleDto.getRoleMenuPermission().forEach((menuId, permissionIds) -> {
                if (menuRoleEntities.stream().noneMatch(menuRoleEntity -> menuRoleEntity.getMenu().getId().equals(menuId))) {
                    menuRepository.findById(menuId).ifPresent(menuEntity -> {
                        MenuRoleEntity menuRoleEntity = MenuRoleEntity.builder()
                                .role(roleEntity)
                                .menu(menuEntity)
                                .build();
                        menuRoleEntities.add(menuRoleEntity);
                    });
                }else {
                    menuRoleEntities.stream().filter(menuRoleEntity -> menuRoleEntity.getMenu().getId().equals(menuId)).findFirst().ifPresent(menuRoleEntity -> {
                        menuRoleEntity.setActive(true);
                    });
                }

                permissionIds.forEach(permissionId -> {
                    if (roleMenuPermissionEntities.stream().noneMatch(roleMenuPermissionEntity -> roleMenuPermissionEntity.getPermission().getId().equals(permissionId))) {
                        permissionRepository.findById(permissionId).ifPresent(permissionEntity -> {
                            RoleMenuPermissionEntity roleMenuPermissionEntity = RoleMenuPermissionEntity.builder()
                                    .menuRole(menuRoleEntities.stream().filter(menuRoleEntity -> menuRoleEntity.getMenu().getId().equals(menuId)).findFirst().orElseThrow(() -> new RuntimeException("Menu not found")))
                                    .permission(permissionEntity)
                                    .isActive(true)
                                    .build();
                            roleMenuPermissionEntities.add(roleMenuPermissionEntity);
                        });
                    }else {
                        roleMenuPermissionEntities.stream().filter(roleMenuPermissionEntity -> roleMenuPermissionEntity.getPermission().getId().equals(permissionId)).findFirst().ifPresent(roleMenuPermissionEntity -> {
                            roleMenuPermissionEntity.setActive(true);
                        });
                    }
                });
            });

            if (roleRepository.save(roleEntity) != null && menuRoleRepository.saveAll(menuRoleEntities) != null && roleMenuPermissionRepository.saveAll(roleMenuPermissionEntities) != null){
                logger.info("Role updated successfully");
                return new ResponseDto("Role updated successfully", 200);
            }else {
                logger.error("Failed to update role");
                return new ResponseDto("Failed to update role", 500);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    public ResponseDto disable(Long id) {
        try {
            Optional<RoleEntity> roleEntity = roleRepository.findById(id);

            if (roleEntity.isPresent()) {
                RoleEntity roleEntityF = roleEntity.get();
                roleEntityF.setActive(false);
                roleEntityF.setModifyBy(CommonUtils.getUser().getUsername());

                if (roleRepository.save(roleEntityF) != null) {
                    logger.info("Role disabled successfully");
                    return ResponseDto.builder().message("Role disabled successfully").status(200).build();
                } else {
                    logger.error("Error occurred while disabling Role");
                    return ResponseDto.builder().message("Error occurred while disabling Role").status(500).build();
                }
            } else {
                logger.error("Role not found");
                return ResponseDto.builder().message("Role not found").status(404).build();
            }
        }catch (Exception e){
            logger.error("Error occurred while disabling Role", e);
            return ResponseDto.builder().message("Error occurred while disabling Role").status(500).build();
        }
    }

    public ResponseDto enable(Long id) {
        try {
            Optional<RoleEntity> roleEntity = roleRepository.findById(id);

            if (roleEntity.isPresent()) {
                RoleEntity roleEntityF = roleEntity.get();
                roleEntityF.setActive(true);
                roleEntityF.setModifyBy(CommonUtils.getUser().getUsername());

                if (roleRepository.save(roleEntityF) != null) {
                    logger.info("Role enabled successfully");
                    return ResponseDto.builder().message("Role enabled successfully").status(200).build();
                } else {
                    logger.error("Error occurred while enabling Role");
                    return ResponseDto.builder().message("Error occurred while enabling Role").status(500).build();
                }
            } else {
                logger.error("Role not found");
                return ResponseDto.builder().message("Role not found").status(404).build();
            }
        }catch (Exception e){
            logger.error("Error occurred while enabling Role", e);
            return ResponseDto.builder().message("Error occurred while enabling Role").status(500).build();
        }
    }

    public ResponseDto paginationRole(PaginationDto paginationDto) {
        try {
            Pageable pageable = CommonUtils.setPagination(paginationDto.getOffset(), paginationDto.getLimit(), paginationDto.getColumnName());
            Page<RoleEntity> roleEntities = roleRepository.findAll(pageable);

            if (roleEntities.isEmpty()) {
                logger.error("No roles found");
                return ResponseDto.builder().message("No roles found").status(404).build();
            }

            List<RoleDto> roleEntityList = new ArrayList<>();

            roleEntities.forEach(roleEntity -> {
                roleEntityList.add(RoleDto.builder()
                                .id(roleEntity.getId())
                                .name(roleEntity.getRoleName())
                        .build());
            });

            HashMap<String, Object> map = new HashMap<>();

            map.put("roles", roleEntityList);
            map.put("rowCount", roleEntities.getTotalElements());

            logger.info("Roles fetched successfully");
            return ResponseDto.builder().data(map).message("Roles fetched successfully").status(200).build();
        } catch (Exception e) {
            logger.error("Error occurred while fetching roles", e);
            return ResponseDto.builder().message("Error occurred while fetching roles").status(500).build();
        }
    }

    public ResponseDto getAllRoles() {
        try {
            List<RoleEntity> roleEntities = roleRepository.findAll();

            if (roleEntities.isEmpty()) {
                logger.error("No roles found");
                return ResponseDto.builder().message("No roles found").status(404).build();
            }

            List<RoleDto> roleEntityList = new ArrayList<>();

            roleEntities.forEach(roleEntity -> {
                roleEntityList.add(RoleDto.builder()
                                .id(roleEntity.getId())
                                .name(roleEntity.getRoleName())
                        .build());
            });

            HashMap<String, Object> map = new HashMap<>();

            map.put("roles", roleEntityList);

            logger.info("Roles fetched successfully");
            return ResponseDto.builder().data(map).message("Roles fetched successfully").status(200).build();
        } catch (Exception e) {
            logger.error("Error occurred while fetching roles", e);
            return ResponseDto.builder().message("Error occurred while fetching roles").status(500).build();
        }
    }
}
