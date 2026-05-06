package com.hcodesolutions.template.service;

import com.hcodesolutions.template.dto.MenuDto;
import com.hcodesolutions.template.dto.PaginationDto;
import com.hcodesolutions.template.dto.ResponseDto;
import com.hcodesolutions.template.dto.SelectByDto;
import com.hcodesolutions.template.entity.MenuEntity;
import com.hcodesolutions.template.entity.PermissionEntity;
import com.hcodesolutions.template.entity.PermissionMenuEntity;
import com.hcodesolutions.template.repository.MenuRepository;
import com.hcodesolutions.template.repository.PermissionMenuRepository;
import com.hcodesolutions.template.repository.PermissionRepository;
import com.hcodesolutions.template.repository.RoleRepository;
import com.hcodesolutions.template.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-19
 * @since 0.0.1
 */
@Service
public class MenuService {
    private static final Logger logger = LoggerFactory.getLogger(MenuService.class);
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;
    private final PermissionMenuRepository permissionMenuRepository;

    public MenuService(RoleRepository roleRepository, PermissionRepository permissionRepository, MenuRepository menuRepository, PermissionMenuRepository permissionMenuRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.menuRepository = menuRepository;
        this.permissionMenuRepository = permissionMenuRepository;
    }

    public ResponseDto saveMenu(MenuDto menuDto) {
        try{
            Optional<String> duplicateField = menuRepository.findDuplicateField(menuDto.getName(), menuDto.getDisplayOrder(), menuDto.getRoute());

            if (duplicateField.isPresent()) {
                logger.error("Duplicate field found for " + duplicateField.get());
                return ResponseDto.builder()
                        .message("Duplicate field found for " + duplicateField.get())
                        .status(400)
                        .build();
            }

            MenuEntity menuEntity = MenuEntity.builder()
                    .description(menuDto.getDescription())
                    .effectiveDate(Date.valueOf(menuDto.getEffectiveDate()))
                    .name(menuDto.getName())
                    .displayOrder(menuDto.getDisplayOrder())
                    .parentMenu(menuDto.getParentMenu() != null ? menuDto.getParentMenu() : null)
                    .route(menuDto.getRoute())
                    .icon(menuDto.getIcon())
                    .createBy(CommonUtils.getUser().getUsername())
                    .isActive(true)
                    .build();

            List<PermissionEntity> permissionEntities = permissionRepository.findByIdInAndIsActive(menuDto.getPermissionIds(), true);

            List<PermissionMenuEntity> permissionMenuEntities = new ArrayList<>();
            for (PermissionEntity permissionEntity : permissionEntities) {
                permissionMenuEntities.add(PermissionMenuEntity.builder()
                        .menu(menuEntity)
                        .permission(permissionEntity)
                        .build());
            }

            if (menuRepository.save(menuEntity) != null && permissionMenuRepository.saveAll(permissionMenuEntities) != null) {
                logger.info("Menu saved successfully");
                return ResponseDto.builder().message("Menu saved successfully").status(200).build();
            } else {
                logger.error("Error occurred while saving menu");
                return ResponseDto.builder().message("Error occurred while saving menu").status(500).build();
            }
        }catch (Exception e){
            logger.error("Error occurred while saving menu", e);
            return ResponseDto.builder().message("Error occurred while saving menu").status(500).build();
        }
    }

    public ResponseDto updateMenu(MenuDto menuDto) {
        try {
            Optional<MenuEntity> menuEntityOptional = menuRepository.findById(menuDto.getId());

            if (menuEntityOptional.isPresent()) {
                MenuEntity menuEntity = menuEntityOptional.get();

                Optional<String> duplicateField = menuRepository.findDuplicateFieldWithoutId(menuDto.getName(), menuDto.getDisplayOrder(), menuDto.getRoute(), menuDto.getId());

                if (duplicateField.isPresent()) {
                    logger.error("Duplicate field found for " + duplicateField.get());
                    return ResponseDto.builder()
                            .message("Duplicate field found for " + duplicateField.get())
                            .status(400)
                            .build();
                }

                menuEntity.setDescription(menuDto.getDescription());
                menuEntity.setEffectiveDate(Date.valueOf(menuDto.getEffectiveDate()));
                menuEntity.setName(menuDto.getName());
                menuEntity.setDisplayOrder(menuDto.getDisplayOrder());
                menuEntity.setParentMenu(menuDto.getParentMenu() != null ? menuDto.getParentMenu() : null);
                menuEntity.setRoute(menuDto.getRoute());
                menuEntity.setIcon(menuDto.getIcon());
                menuEntity.setModifyBy(CommonUtils.getUser().getUsername());

                List<PermissionEntity> permissionEntities = permissionRepository.findByIdInAndIsActive(menuDto.getPermissionIds(), true);

                List<PermissionMenuEntity> permissionMenuEntities = permissionMenuRepository.findByMenuId(menuEntity.getId());

                permissionMenuEntities.forEach(permissionMenuEntity -> {
                    permissionMenuEntity.setActive(false);
                });

                for (PermissionEntity permissionEntity : permissionEntities) {
                    Optional<PermissionMenuEntity> permissionMenuEntityOptional = permissionMenuEntities.stream().filter(permissionMenuEntity -> permissionMenuEntity.getPermission().getId().equals(permissionEntity.getId())).findFirst();
                    if (permissionMenuEntityOptional.isPresent()) {
                        permissionMenuEntityOptional.get().setActive(true);
                    } else {
                        permissionMenuEntities.add(PermissionMenuEntity.builder()
                                .menu(menuEntity)
                                .permission(permissionEntity)
                                .build());
                    }
                }

                if (menuRepository.save(menuEntity) != null && permissionMenuRepository.saveAll(permissionMenuEntities) != null) {
                    logger.info("Menu updated successfully");
                    return ResponseDto.builder().message("Menu updated successfully").status(200).build();
                } else {
                    logger.error("Error occurred while updating menu");
                    return ResponseDto.builder().message("Error occurred while updating menu").status(500).build();
                }
            } else {
                logger.error("Menu not found");
                return ResponseDto.builder().message("Menu not found").status(404).build();
            }
        }catch (Exception e){
            logger.error("Error occurred while updating menu", e);
            return ResponseDto.builder().message("Error occurred while updating menu").status(500).build();
        }
    }

    public ResponseDto disable(Long id) {
        try {
            Optional<MenuEntity> menuEntityOptional = menuRepository.findById(id);

            if (menuEntityOptional.isPresent()) {
                MenuEntity menuEntity = menuEntityOptional.get();
                menuEntity.setActive(false);
                menuEntity.setModifyBy(CommonUtils.getUser().getUsername());

                if (menuRepository.save(menuEntity) != null) {
                    logger.info("Menu disabled successfully");
                    return ResponseDto.builder().message("Menu disabled successfully").status(200).build();
                } else {
                    logger.error("Error occurred while disabling menu");
                    return ResponseDto.builder().message("Error occurred while disabling menu").status(500).build();
                }
            } else {
                logger.error("Menu not found");
                return ResponseDto.builder().message("Menu not found").status(404).build();
            }
        }catch (Exception e){
            logger.error("Error occurred while disabling menu", e);
            return ResponseDto.builder().message("Error occurred while disabling menu").status(500).build();
        }
    }

    public ResponseDto enable(Long id) {
        try {
            Optional<MenuEntity> menuEntityOptional = menuRepository.findById(id);

            if (menuEntityOptional.isPresent()) {
                MenuEntity menuEntity = menuEntityOptional.get();
                menuEntity.setActive(true);
                menuEntity.setModifyBy(CommonUtils.getUser().getUsername());

                if (menuRepository.save(menuEntity) != null) {
                    logger.info("Menu enabled successfully");
                    return ResponseDto.builder().message("Menu enabled successfully").status(200).build();
                } else {
                    logger.error("Error occurred while enabling menu");
                    return ResponseDto.builder().message("Error occurred while enabling menu").status(500).build();
                }
            } else {
                logger.error("Menu not found");
                return ResponseDto.builder().message("Menu not found").status(404).build();
            }
        }catch (Exception e){
            logger.error("Error occurred while enabling menu", e);
            return ResponseDto.builder().message("Error occurred while enabling menu").status(500).build();
        }
    }

    public ResponseDto paginationMenu(PaginationDto paginationDto) {
        try {
            Pageable pageable = CommonUtils.setPagination(paginationDto.getOffset(), paginationDto.getLimit(), paginationDto.getColumnName());
            Page<MenuEntity> menuEntities = menuRepository.findAll(pageable);

            if (menuEntities.hasContent()) {
                List<MenuDto> menuDtos = new ArrayList<>();
                for (MenuEntity menuEntity : menuEntities.getContent()) {
                    menuDtos.add(MenuDto.builder()
                            .id(menuEntity.getId())
                            .description(menuEntity.getDescription())
                            .effectiveDate(String.valueOf(menuEntity.getEffectiveDate()))
                            .name(menuEntity.getName())
                            .displayOrder(menuEntity.getDisplayOrder())
                            .parentMenu(menuEntity.getParentMenu() != null ? menuEntity.getParentMenu() : null)
                            .route(menuEntity.getRoute())
                            .icon(menuEntity.getIcon())
                            .isActive(menuEntity.isActive())
                            .build());
                }
                logger.info("Menu pagination success");
                return ResponseDto.builder().message("Menu pagination success").status(200).data(new HashMap<>(Map.of("menu", menuDtos, "rowCount", menuEntities.getTotalElements()))).build();
            } else {
                logger.error("Menu not found");
                return ResponseDto.builder().message("Menu not found").status(404).build();
            }
        }catch (Exception e){
            logger.error("Error occurred while paginating menu", e);
            return ResponseDto.builder().message("Error occurred while paginating menu").status(500).build();
        }
    }

    public ResponseDto getAllMenus() {
        try {
            List<MenuEntity> menuEntities = menuRepository.findAll();

            if (!menuEntities.isEmpty()) {
                List<MenuDto> menuDtos = new ArrayList<>();
                for (MenuEntity menuEntity : menuEntities) {
                    menuDtos.add(MenuDto.builder()
                            .id(menuEntity.getId())
                            .description(menuEntity.getDescription())
                            .effectiveDate(String.valueOf(menuEntity.getEffectiveDate()))
                            .name(menuEntity.getName())
                            .displayOrder(menuEntity.getDisplayOrder())
                            .parentMenu(menuEntity.getParentMenu() != null ? menuEntity.getParentMenu() : null)
                            .route(menuEntity.getRoute())
                            .icon(menuEntity.getIcon())
                            .isActive(menuEntity.isActive())
                            .build());
                }
                logger.info("Menu list success");
                return ResponseDto.builder()
                        .message("Menu list success")
                        .status(200)
                        .data(new HashMap<>(Map.of("menus", menuDtos)))
                        .build();
            } else {
                logger.error("Menu not found");
                return ResponseDto.builder().message("Menu not found").status(404).build();
            }
        }catch (Exception e){
            logger.error("Error occurred while getting menu list", e);
            return ResponseDto.builder().message("Error occurred while getting menu list").status(500).build();
        }
    }

    public ResponseDto findByType(SelectByDto selectByDto){
        try{
            MenuEntity menuEntity = null;

            if (selectByDto.getSelectedType().equalsIgnoreCase("name")) {
                menuEntity = menuRepository.findByName(selectByDto.getSelectedValue()).orElse(null);
            } else if (selectByDto.getSelectedType().equalsIgnoreCase("route")) {
                menuEntity = menuRepository.findByRoute(selectByDto.getSelectedValue()).orElse(null);
            }

            if (menuEntity != null) {
                MenuDto menuDto = MenuDto.builder()
                        .id(menuEntity.getId())
                        .description(menuEntity.getDescription())
                        .effectiveDate(String.valueOf(menuEntity.getEffectiveDate()))
                        .name(menuEntity.getName())
                        .displayOrder(menuEntity.getDisplayOrder())
                        .parentMenu(menuEntity.getParentMenu() != null ? menuEntity.getParentMenu() : null)
                        .route(menuEntity.getRoute())
                        .icon(menuEntity.getIcon())
                        .isActive(menuEntity.isActive())
                        .build();
                logger.info("Menu found successfully");
                return ResponseDto.builder().message("Menu found successfully").status(200).data(new HashMap<>(Map.of("menu", menuDto))).build();
            } else {
                logger.error("Menu not found");
                return ResponseDto.builder().message("Menu not found").status(404).build();
            }
        }catch (Exception e){
            logger.error("Error occurred while getting menu list", e);
            return ResponseDto.builder().message("Error occurred while getting menu list").status(500).build();
        }
    }

}
