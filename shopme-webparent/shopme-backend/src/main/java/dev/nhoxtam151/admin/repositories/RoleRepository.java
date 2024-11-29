package dev.nhoxtam151.admin.repositories;

import dev.nhoxtam151.shopmecommon.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Optional<Role> findByName(Role.RoleType name);
}
