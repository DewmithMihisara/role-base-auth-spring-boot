package com.hcodesolutions.template.repository;

import com.hcodesolutions.template.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-05
 * @since 0.0.1
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
