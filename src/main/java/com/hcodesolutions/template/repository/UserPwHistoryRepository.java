package com.hcodesolutions.template.repository;

import com.hcodesolutions.template.entity.UserPwHistoryEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@Repository
public interface UserPwHistoryRepository extends JpaRepository<UserPwHistoryEntity, Long> {
    List<UserPwHistoryEntity> findTop5ByUserIdOrderByPwChangedDateDesc(Long id, PageRequest of);
}
