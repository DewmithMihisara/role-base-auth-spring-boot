package com.hcodesolutions.template.service;

import com.hcodesolutions.template.dto.AuthReqDto;
import com.hcodesolutions.template.dto.AuthResDto;
import com.hcodesolutions.template.entity.UserEntity;
import com.hcodesolutions.template.repository.UserRepository;
import com.hcodesolutions.template.security.Encoder;
import com.hcodesolutions.template.util.JWTUtil;
import com.hcodesolutions.template.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-07
 * @since 0.0.1
 */
@Service
public class AuthService {
    @Value("${login.success.message}")
    private String successMessage;

    @Value("${login.incorrect.message}")
    private String incorrectMessage;

    @Value("${login.lock.message}")
    private String accountLockedMessage;

    @Value("${user.notfound.message}")
    private String notFoundMessage;

    private final UserRepository userRepository;
    private final Encoder encoder;
    private final JWTUtil jwtUtil;

    public AuthService(UserRepository userRepository, Encoder encoder, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public ResponseUtil<AuthResDto> authenticationLogin(AuthReqDto authReqDTO) throws Exception {
        try {
            Optional<UserEntity> userEntity = userRepository.findByEmailAndIsActive(authReqDTO.getEmail(), true);
            if (userEntity.isPresent()) {
                if (!userEntity.get().isLocked()) {
                    boolean isCorrect = encoder.matches(authReqDTO.getPassword().trim(), userEntity.get().getPassword());

                    if (isCorrect) {
                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("Role", userEntity.get().getRole().getRoleName());

                        //token generate
                        String token = jwtUtil.generateJwtToken(userEntity.get().getUserName(), hashMap);

                        //logger.info(token);
                        AuthResDto authResDTO = new AuthResDto();
                        authResDTO.setId(userEntity.get().getId());
                        authResDTO.setName(userEntity.get().getFirstName());
//                        authResDTO.setRole(userEntity.get().getRole().getRoleName());
                        authResDTO.setToken(token);
                        authResDTO.setEmail(userEntity.get().getEmail());

                        return new ResponseUtil<>(200, successMessage, authResDTO);

                    } else {
                        throw new RuntimeException("#USER_ERROR#" + incorrectMessage);
                    }
                } else {
                    throw new RuntimeException("#USER_ERROR#" + accountLockedMessage);
                }
            } else {
                throw new IllegalArgumentException("#USER_ERROR#" + notFoundMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("#USER_ERROR#")) {
                e = new RuntimeException(e.getMessage().substring(12));
                throw e;
            } else {
                throw new IllegalArgumentException(notFoundMessage);
            }
        }
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = null;
        try {
            user = userRepository.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user != null) {
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(email,
                    user.getPassword(), new ArrayList<>());
            return userDetails;
        } else {
            throw new UsernameNotFoundException(email);
        }
    }
}
