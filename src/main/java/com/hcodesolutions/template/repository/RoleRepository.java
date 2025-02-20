package com.hcodesolutions.template.repository;

import com.hcodesolutions.template.entity.RoleEntity;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByRoleName(String name);

    Optional<RoleEntity> findByRoleNameAndIdNot(String name, Long id);
}
