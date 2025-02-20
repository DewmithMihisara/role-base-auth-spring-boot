package com.hcodesolutions.template.repository;

import com.hcodesolutions.template.entity.MenuEntity;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-20
 * @since 0.0.1
 */
@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    @Query(value = """
        SELECT 
            CASE 
                WHEN m.name = :name THEN 'NAME' 
                WHEN m.display_order = :displayOrder THEN 'DISPLAY_ORDER' 
                WHEN m.route = :route THEN 'ROUT'
            END AS duplicateField 
        FROM menu m 
        WHERE m.name = :name OR m.display_order = :displayOrder OR m.route = :route
        LIMIT 1
    """, nativeQuery = true)
    Optional<String> findDuplicateField(
            @NotEmpty(message = "name is required")@Param("name") String name,
            @NotEmpty(message = "display order is required")@Param("displayOrder") Integer displayOrder,
            @NotEmpty(message = "rout is required")@Param("route") String route
    );

    @Query(value = """
        SELECT 
            CASE 
                WHEN m.name = :name THEN 'NAME' 
                WHEN m.display_order = :displayOrder THEN 'DISPLAY_ORDER' 
                WHEN m.route = :route THEN 'ROUT'
            END AS duplicateField 
        FROM menu m 
        WHERE (m.name = :name OR m.display_order = :displayOrder OR m.route = :route)
        AND m.menu_id != :id
        LIMIT 1
    """, nativeQuery = true)
    Optional<String> findDuplicateFieldWithoutId(
            @NotEmpty(message = "name is required")@Param("name") String name,
            @NotEmpty(message = "display order is required")@Param("displayOrder") Integer displayOrder,
            @NotEmpty(message = "rout is required")@Param("route") String route,
            @Param("id") Long id
    );

    Optional<MenuEntity> findByName(String selectedValue);

    Optional<MenuEntity> findByRoute(String selectedValue);
}
