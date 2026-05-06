package com.hcodesolutions.template.controller;

import com.hcodesolutions.template.dto.MenuDto;
import com.hcodesolutions.template.dto.PaginationDto;
import com.hcodesolutions.template.dto.ResponseDto;
import com.hcodesolutions.template.dto.SelectByDto;
import com.hcodesolutions.template.service.MenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-20
 * @since 0.0.1
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/menu")
@Tag(name = "menu controller", description = "menu related operations...")
public class MenuController {
    private static Logger logger = LoggerFactory.getLogger(MenuController.class);
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public ResponseDto saveOrUpdateMenu(@RequestBody MenuDto menuDto) {
        try {
            if (menuDto.getId() == null) {
                return menuService.saveMenu(menuDto);
            } else {
                return menuService.updateMenu(menuDto);
            }
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/dis/{id}")
    public ResponseDto deleteMenu(@PathVariable Long id) {
        try {
            return menuService.disable(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/enb/{id}")
    public ResponseDto enableMenu(@PathVariable Long id) {
        try {
            return menuService.enable(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/select_by_type")
    public ResponseDto getSelectedMenu(@RequestBody SelectByDto selectByDto) {
        try {
            return menuService.findByType(selectByDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/all")
    public ResponseDto getAllMenus(@RequestBody(required = false) PaginationDto paginationDto) {
        try {
            if (paginationDto != null) {
                return menuService.paginationMenu(paginationDto);
            }else {
                return menuService.getAllMenus();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }
}
