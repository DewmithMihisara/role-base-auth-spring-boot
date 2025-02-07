package com.hcodesolutions.template.repository;

import com.hcodesolutions.template.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-05
 * @since 0.0.1
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByToken(String substring);

    @Query(value = """
    SELECT 
        CASE 
            WHEN u.user_name = :userName THEN 'USERNAME' 
            WHEN u.email = :email THEN 'EMAIL' 
            WHEN u.contact_number = :contactNumber THEN 'CONTACT_NUMBER' 
        END AS duplicateField 
    FROM user_entity u 
    WHERE u.user_name = :userName OR u.email = :email OR u.contact_number = :contactNumber
    LIMIT 1
""", nativeQuery = true)
    Optional<String> findDuplicateField(@Param("userName") String userName,
                                        @Param("email") String email,
                                        @Param("contactNumber") String contactNumber);

    UserEntity findByUserName(String selectedValue);

    UserEntity findByEmail(String selectedValue);

    UserEntity findById(long l);

    List<UserEntity> findAllByIsActive(boolean b);

    Optional<UserEntity> findByToken(String token);

    Optional<UserEntity> findByEmailAndIsActive(String email, boolean b);
}
