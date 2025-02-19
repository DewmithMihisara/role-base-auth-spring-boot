package com.hcodesolutions.template.service;

import com.hcodesolutions.template.dto.AuthReqDto;
import com.hcodesolutions.template.dto.AuthResDto;
import com.hcodesolutions.template.entity.UserEntity;
import com.hcodesolutions.template.repository.UserRepository;
import com.hcodesolutions.template.repository.UserRoleRepository;
import com.hcodesolutions.template.security.Encoder;
import com.hcodesolutions.template.util.JWTUtil;
import com.hcodesolutions.template.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-07
 * @since 0.0.1
 */
@Service
public class AuthService {
    private final UserRoleRepository userRoleRepository;
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

    public AuthService(UserRepository userRepository, Encoder encoder, JWTUtil jwtUtil, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.userRoleRepository = userRoleRepository;
    }

    public ResponseUtil<AuthResDto> authenticationLogin(AuthReqDto authReqDTO) throws Exception {
        try {
            Optional<UserEntity> userEntity = userRepository.findByEmailAndIsActive(authReqDTO.getEmail(), true);

            if (userEntity.isPresent()) {
                System.out.println(userEntity.get().getFirstName());

                if (!userEntity.get().isLocked()) {
                    boolean isCorrect = encoder.matches(authReqDTO.getPassword().trim(), userEntity.get().getPassword());

                    System.out.println(isCorrect);
                    if (isCorrect) {

                        HashMap<String, Object> hashMap = new HashMap<>();

                        List<String> roles = new ArrayList<>();
                        userRoleRepository.findByUserIdAndIsActive(userEntity.get().getId(), true).forEach(userRoleEntity -> {
                            roles.add(userRoleEntity.getRole().getRoleName());
                        });

                        hashMap.put("Role", roles);
                        //token generate
                        String token = jwtUtil.generateJwtToken(userEntity.get().getUserName(), hashMap);

                        //logger.info(token);
                        AuthResDto authResDTO = new AuthResDto();
                        authResDTO.setId(userEntity.get().getId());
                        authResDTO.setName(userEntity.get().getFirstName());
                        authResDTO.setRole(roles);
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
        } catch (Exception exception) {
            if (exception.getMessage().contains("#USER_ERROR#")) {
                throw new RuntimeException(exception.getMessage().substring(12));
            } else {
                throw new IllegalArgumentException(notFoundMessage);
            }
        }
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }

        List<String> roles = new ArrayList<>();

        userRoleRepository.findByUserIdAndIsActive(user.getId(), true).forEach(userRoleEntity -> {
            roles.add(userRoleEntity.getRole().getRoleName());
        });

        // Convert roles from the database to GrantedAuthority objects
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role)) // Assuming role.getName() returns "USER", "ADMIN"
                .collect(Collectors.toList());

        return new User(email, user.getPassword(), authorities);
    }
}
