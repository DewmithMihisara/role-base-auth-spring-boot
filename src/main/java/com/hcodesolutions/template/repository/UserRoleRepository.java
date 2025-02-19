package com.hcodesolutions.template.repository;

import com.hcodesolutions.template.entity.UserEntity;
import com.hcodesolutions.template.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    List<UserRoleEntity> findByUser(UserEntity user);

    List<UserRoleEntity> findByUserIdAndIsActive(Long id, boolean b);
}
