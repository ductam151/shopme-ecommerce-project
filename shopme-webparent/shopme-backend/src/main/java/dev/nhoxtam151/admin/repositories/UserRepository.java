package dev.nhoxtam151.admin.repositories;

import dev.nhoxtam151.admin.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Long countById(Long id);

    @Query("UPDATE User u SET u.enabled = ?2  WHERE u.id = ?1")
    @Modifying
    void updateEnabledStatus(Long userId, boolean enabled);

    @Query("FROM User u  WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
        //@Query("FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1% OR u.email LIKE %?1%")
    Page<User> findAll(String keyword, Pageable pageable);


}
