package com.hcodesolutions.template.repository;

import com.hcodesolutions.template.entity.UserPwHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-21
 * @since 0.0.1
 */
@Repository
public interface UserPasswordRepository extends JpaRepository<UserPwHistoryEntity, Long> {
}
