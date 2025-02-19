package com.hcodesolutions.template.controller;

import com.hcodesolutions.template.dto.AuthReqDto;
import com.hcodesolutions.template.service.AuthService;
import com.hcodesolutions.template.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
@Tag(name = "auth controller", description = "authentication related operations...")
public class AuthController {
    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseUtil<?>> validateAuthentication(@RequestBody AuthReqDto authReqDTO) {
        try {
            logger.info("/login : " + authReqDTO.getEmail().toString());
            ResponseUtil<?> util = authService.authenticationLogin(authReqDTO);
            return ResponseEntity.ok(util);
        } catch (Exception e) {
            logger.error("/login : " +e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}
