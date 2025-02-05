package com.hcodesolutions.template.controller;

import com.hcodesolutions.template.dto.*;
import com.hcodesolutions.template.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseDto saveOrUpdateUsr(@RequestBody UserDto userDTO) {
        try {
            if (userDTO.getId() == null) {
                return userService.saveUser(userDTO);
            } else {
                return userService.updateUser(userDTO);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/dis/{id}")
    public ResponseDto deleteUser(@PathVariable Long id) {
        try {
            return userService.disableUser(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/enb/{id}")
    public ResponseDto enableUser(@PathVariable Long id) {
        try {
            return userService.enableUser(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @PostMapping("/select_by_type")
    public ResponseDto selectByType(@RequestBody SelectByDto selectByDto) {
        try {
            return userService.getUserByType(selectByDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @PostMapping("/all")
    public ResponseDto getAllUsers(@RequestBody(required = false) PaginationDto paginationDto) {
        try {
            if (paginationDto != null) {
                return userService.findByPagination(paginationDto);
            }else {
                return userService.findAllUsers();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @PostMapping("/change_password")
    public ResponseDto changePassword(@RequestBody PasswordDto passwordDto) {
        try {
            return userService.changePw(passwordDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @PostMapping("/set_password")
    public ResponseDto setPassword(@RequestBody ConformPasswordDto passwordDto) {
        try {
            return userService.setPassword(passwordDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    @GetMapping("/unlocked/{id}")
    public ResponseDto unlockUser(@PathVariable Long id) {
        try {
            return userService.unlockedUsers(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }
}
