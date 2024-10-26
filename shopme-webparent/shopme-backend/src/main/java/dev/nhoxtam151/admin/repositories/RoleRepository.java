package dev.nhoxtam151.admin.repositories;

import dev.nhoxtam151.shopmecommon.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
