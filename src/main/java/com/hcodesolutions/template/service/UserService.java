package com.hcodesolutions.template.service;

import com.hcodesolutions.template.dto.*;
import com.hcodesolutions.template.entity.RoleEntity;
import com.hcodesolutions.template.entity.UserEntity;
import com.hcodesolutions.template.entity.UserPwHistoryEntity;
import com.hcodesolutions.template.entity.UserRoleEntity;
import com.hcodesolutions.template.repository.RoleRepository;
import com.hcodesolutions.template.repository.UserPwHistoryRepository;
import com.hcodesolutions.template.repository.UserRepository;
import com.hcodesolutions.template.repository.UserRoleRepository;
import com.hcodesolutions.template.dto.ConformPasswordDto;
import com.hcodesolutions.template.util.CommonUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-05
 * @since 0.0.1
 */
@Service
@Transactional
public class UserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPwHistoryRepository userPwHistoryRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, UserPwHistoryRepository userPwHistoryRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userPwHistoryRepository = userPwHistoryRepository;
    }

    public ResponseDto saveUser(UserDto userDto) {
        try {
            Optional<String> duplicateField = userRepository.findDuplicateField(
                    userDto.getUserName(), userDto.getEmail(), userDto.getContactNumber());

            if (duplicateField.isPresent()) {
                logger.error("Duplicate field found for " + duplicateField.get());
                return ResponseDto.builder()
                        .message("Duplicate field found for " + duplicateField.get())
                        .status(400)
                        .build();
            }

//        String substring;
//        do {
//            substring = UUID.randomUUID().toString().substring(0, 10);
//        } while (userRepository.existsByToken(substring));

            UserEntity user = UserEntity.builder()
                    .firstName(userDto.getFirstName())
                    .lastName(userDto.getLastName())
                    .userName(userDto.getEmail())
                    .email(userDto.getEmail())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .contactNumber(userDto.getContactNumber())
                    .tryCount(0)
                    .isLocked(false)
                    .createBy(CommonUtils.getUser().getUsername())
                    .isActive(true)
//                .token(substring)
                    .build();

            List<UserRoleEntity> userRoleEntities = new ArrayList<>();

            userDto.getRoleIds().forEach(roleId -> {
                RoleEntity roleEntity = roleRepository.findById(roleId).orElse(null);
                if (roleEntity != null) {
                    userRoleEntities.add(UserRoleEntity.builder()
                            .user(user)
                            .role(roleEntity)
                            .isActive(true)
                            .build());
                } else {
                    logger.error("Role not found for id: " + roleId);
                    throw new RuntimeException("Role not found for id: " + roleId);
                }
            });

            if (userRepository.save(user) != null && userRoleRepository.saveAll(userRoleEntities) != null) {
                // need to send emails if password set via email

                logger.info("User saved successfully");
                return ResponseDto.builder()
                        .message("User saved successfully")
                        .status(200)
                        .build();
            } else {
                logger.error("User save failed");
                return ResponseDto.builder()
                        .message("User save failed")
                        .status(500)
                        .build();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseDto.builder()
                    .message(e.getMessage())
                    .status(500)
                    .build();
        }
    }

    public ResponseDto updateUser(UserDto userDto) {
        try {
            Optional<UserEntity> userOptional = userRepository.findById(userDto.getId());

            if (userOptional.isEmpty()) {
                logger.error("User not found for id: " + userDto.getId());
                return ResponseDto.builder()
                        .message("User not found for id: " + userDto.getId())
                        .status(404)
                        .build();
            }

            UserEntity user = userOptional.get();

            Optional<String> duplicateField = userRepository.findDuplicateFieldWithoutId(
                    userDto.getUserName(), userDto.getEmail(), userDto.getContactNumber(), userDto.getId());

            if (duplicateField.isPresent()) {
                logger.error("Duplicate field found for " + duplicateField.get());
                return ResponseDto.builder()
                        .message("Duplicate field found for " + duplicateField.get())
                        .status(400)
                        .build();
            }

            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setUserName(userDto.getUserName());
            user.setEmail(userDto.getEmail());
            user.setContactNumber(userDto.getContactNumber());
            user.setModifyBy(CommonUtils.getUser().getUsername());

            List<UserRoleEntity> userRoleEntities = userRoleRepository.findByUser(user);

            userRoleEntities.forEach(userRoleEntity -> {
                userRoleEntity.setActive(false);
            });

            userDto.getRoleIds().forEach(roleId -> {
                if (userRoleEntities.stream().anyMatch(userRoleEntity -> userRoleEntity.getRole().getId().equals(roleId))) {
                    userRoleEntities.stream()
                            .filter(userRoleEntity -> userRoleEntity.getRole().getId().equals(roleId))
                            .findFirst()
                            .ifPresent(userRoleEntity -> userRoleEntity.setActive(true));
                } else {
                    RoleEntity roleEntity = roleRepository.findById(roleId).orElse(null);
                    if (roleEntity != null) {
                        userRoleEntities.add(UserRoleEntity.builder()
                                .user(user)
                                .role(roleEntity)
                                .build());
                    } else {
                        logger.error("Role not found for id: " + roleId);
                        throw new RuntimeException("Role not found for id: " + roleId);
                    }
                }
            });

            if (userRepository.save(user) != null && userRoleRepository.saveAll(userRoleEntities) != null) {
                logger.info("User updated successfully");
                return ResponseDto.builder()
                        .message("User updated successfully")
                        .status(200)
                        .build();
            } else {
                logger.error("User update failed");
                return ResponseDto.builder()
                        .message("User update failed")
                        .status(500)
                        .build();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseDto.builder()
                    .message(e.getMessage())
                    .status(500)
                    .build();
        }
    }

    public ResponseDto disableUser(Long id) {
        try {
            Optional<UserEntity> userOptional = userRepository.findById(id);

            if (userOptional.isEmpty()) {
                logger.error("User not found for id: " + id);
                return ResponseDto.builder()
                        .message("User not found for id: " + id)
                        .status(404)
                        .build();
            }

            UserEntity user = userOptional.get();
            user.setActive(false);

            if (userRepository.save(user) != null) {
                logger.info("User disabled successfully");
                return ResponseDto.builder()
                        .message("User disabled successfully")
                        .status(200)
                        .build();
            } else {
                logger.error("User disable failed");
                return ResponseDto.builder()
                        .message("User disable failed")
                        .status(500)
                        .build();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseDto.builder()
                    .message(e.getMessage())
                    .status(500)
                    .build();
        }
    }

    public ResponseDto enableUser(Long id) {
        try {
            Optional<UserEntity> userOptional = userRepository.findById(id);

            if (userOptional.isEmpty()) {
                logger.error("User not found for id: " + id);
                return ResponseDto.builder()
                        .message("User not found for id: " + id)
                        .status(404)
                        .build();
            }

            UserEntity user = userOptional.get();
            user.setActive(true);

            if (userRepository.save(user) != null) {
                logger.info("User enabled successfully");
                return ResponseDto.builder()
                        .message("User enabled successfully")
                        .status(200)
                        .build();
            } else {
                logger.error("User enable failed");
                return ResponseDto.builder()
                        .message("User enable failed")
                        .status(500)
                        .build();
            }
        }catch (Exception e){
            return ResponseDto.builder()
                    .message(e.getMessage())
                    .status(500)
                    .build();
        }
    }

    public ResponseDto getUserByType(SelectByDto selectUserDTO) {
        try {
            UserEntity result = null;

            if (selectUserDTO.getSelectedType().equalsIgnoreCase("username")) {
                result = userRepository.findByUserName(selectUserDTO.getSelectedValue());
            } else if (selectUserDTO.getSelectedType().equalsIgnoreCase("email")) {
                result = userRepository.findByEmail(selectUserDTO.getSelectedValue());
            } else if (selectUserDTO.getSelectedType().equalsIgnoreCase("id")) {
                result = userRepository.findById(Long.parseLong(selectUserDTO.getSelectedValue()));
            } else {
                logger.error("Invalid type");
                return ResponseDto.builder()
                        .message("Invalid type")
                        .status(400)
                        .build();
            }

            if (result != null) {
                UserDto userDto = UserDto.builder()
                        .id(result.getId())
                        .firstName(result.getFirstName())
                        .lastName(result.getLastName())
                        .userName(result.getUserName())
                        .email(result.getEmail())
                        .contactNumber(result.getContactNumber())
                        .tryCount(result.getTryCount())
                        .isLocked(result.isLocked())
                        .createBy(result.getCreateBy())
                        .modifyBy(result.getModifyBy())
                        .isActive(result.isActive())
                        .build();

                logger.info("User found");
                return ResponseDto.builder()
                        .message("User found")
                        .status(200)
                        .data(new HashMap<>(Map.of("user", userDto)))
                        .build();
            } else {
                logger.error("User not found");
                return ResponseDto.builder()
                        .message("User not found")
                        .status(404)
                        .build();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseDto.builder()
                    .message(e.getMessage())
                    .status(500)
                    .build();
        }
    }

    public ResponseDto findAllUsers() {
        try {
            List<UserDto> list = new ArrayList<>();
            userRepository.findAllByIsActive(true).forEach(userEntity -> {
                UserDto userDto = UserDto.builder()
                        .id(userEntity.getId())
                        .firstName(userEntity.getFirstName())
                        .lastName(userEntity.getLastName())
                        .userName(userEntity.getUserName())
                        .email(userEntity.getEmail())
                        .contactNumber(userEntity.getContactNumber())
                        .tryCount(userEntity.getTryCount())
                        .isLocked(userEntity.isLocked())
                        .createBy(userEntity.getCreateBy())
                        .modifyBy(userEntity.getModifyBy())
                        .isActive(userEntity.isActive())
                        .build();
            });

            HashMap<String, Object> map = new HashMap<>();
            map.put("users", list);
            logger.info("Users found successfully");
            return new ResponseDto("Users found successfully", 200, map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    public ResponseDto findByPagination(PaginationDto paginationDTO) {
        try {
            List<UserDto> list = new ArrayList<>();
            userRepository.findAll(CommonUtils.setPagination(paginationDTO.getOffset(), paginationDTO.getLimit(), paginationDTO.getColumnName())).forEach(userEntity -> {
                UserDto userDto = UserDto.builder()
                        .id(userEntity.getId())
                        .firstName(userEntity.getFirstName())
                        .lastName(userEntity.getLastName())
                        .userName(userEntity.getUserName())
                        .email(userEntity.getEmail())
                        .contactNumber(userEntity.getContactNumber())
                        .tryCount(userEntity.getTryCount())
                        .isLocked(userEntity.isLocked())
                        .createBy(userEntity.getCreateBy())
                        .modifyBy(userEntity.getModifyBy())
                        .isActive(userEntity.isActive())
                        .build();
                list.add(userDto);
            });

            HashMap<String, Object> map = new HashMap<>();
            map.put("users", list);
            logger.info("Users found successfully");
            return new ResponseDto("Users found successfully", 200, map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    public ResponseDto changePw(PasswordDto passwordDTO) {
        try {
            if (userRepository.existsById(passwordDTO.getId())) {
                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

                List<UserPwHistoryEntity> lastFivePasswords = userPwHistoryRepository.findTop5ByUserIdOrderByPwChangedDateDesc(
                        passwordDTO.getId(), PageRequest.of(0, 5));

                if (lastFivePasswords.stream().anyMatch(pwHistory -> bcrypt.matches(passwordDTO.getPassword(), pwHistory.getOldPassword()))) {
                    logger.error("Password already used");
                    return new ResponseDto("Password already used", 500);
                } else {
                    UserEntity userEntity = userRepository.findById(passwordDTO.getId()).orElse(null);
                    if (userEntity != null) {
                        String encodedPassword = passwordEncoder.encode(passwordDTO.getPassword());

                        userEntity.setPassword(encodedPassword);
//                        userEntity.setModifyBy(CommonUtils.getUser().getUsername());
                        userRepository.save(userEntity);

                        lastFivePasswords.forEach(pwHistory -> {
                            pwHistory.setActive(false);
                            userPwHistoryRepository.save(pwHistory);
                        });

                        UserPwHistoryEntity newHistoryEntry = UserPwHistoryEntity.builder()
                                .oldPassword(encodedPassword)
                                .user(userEntity)
                                .pwChangedDate(java.util.Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
//                                .createBy(CommonUtils.getUser().getUsername())
                                .isActive(true)
                                .build();


                        UserPwHistoryEntity savedEntry = userPwHistoryRepository.save(newHistoryEntry);
                        if (savedEntry != null) {
                            logger.info("Password changed successfully");
                            return new ResponseDto("Password changed successfully", 200);
                        } else {
                            logger.error("Password history not saved");
                            return new ResponseDto("Password history not saved", 500);
                        }
                    } else {
                        logger.error("User not found");
                        return new ResponseDto("User not found", 404);
                    }
                }
            }
            logger.error("User not found");
            return new ResponseDto("User not found", 404);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    public ResponseDto setPassword(ConformPasswordDto conformPasswordDTO) {
        try {
            UserEntity userEntity = userRepository.findByToken(conformPasswordDTO.getToken()).orElse(null);
            if (userEntity != null) {
                if (userEntity.getPassword() != null) {
                    logger.error("Password already set");
                    return new ResponseDto("Password already set", 500);
                } else {
                    userEntity.setPassword(passwordEncoder.encode(conformPasswordDTO.getPassword()));
                    userRepository.save(userEntity);

                    userPwHistoryRepository.save(UserPwHistoryEntity.builder()
                            .oldPassword(conformPasswordDTO.getPassword())
                            .user(userEntity)
                            .pwChangedDate(java.util.Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                            .createBy(userEntity.getEmail())
                            .isActive(true)
                            .build());

                    logger.info("Password set successfully");
                    return new ResponseDto("Password set successfully", 200);
                }
            } else {
                logger.error("User not found");
                return new ResponseDto("User not found", 404);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }

    public ResponseDto unlockedUsers(Long id) {
        try {
            UserEntity userEntity = userRepository.findById(id).orElse(null);
            if (userEntity != null) {
                userEntity.setLocked(false);
//                userEntity.setModifyBy(CommonUtils.getUser().getUsername());
                userRepository.save(userEntity);
                logger.info("User unlocked successfully");
                return new ResponseDto("User unlocked successfully", 200);
            }
            logger.error("User not found");
            return new ResponseDto("User not found", 404);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseDto(e.getMessage(), 500);
        }
    }



}
