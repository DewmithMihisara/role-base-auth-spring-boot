package com.hcodesolutions.template.repository;

import com.hcodesolutions.template.entity.MenuEntity;
import com.hcodesolutions.template.entity.PermissionMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-20
 * @since 0.0.1
 */
@Repository
public interface PermissionMenuRepository extends JpaRepository<PermissionMenuEntity, Long> {
    List<PermissionMenuEntity> findByMenuId(Long id);
}
