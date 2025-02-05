package com.hcodesolutions.template.repository;

import com.hcodesolutions.template.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
