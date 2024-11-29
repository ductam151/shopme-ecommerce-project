package dev.nhoxtam151.admin;

import dev.nhoxtam151.admin.repositories.RoleRepository;
import dev.nhoxtam151.shopmecommon.models.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository repository;

    @Test
    @DisplayName("insertRoleTest")
    public void test1() {
        Role role = new Role();
        role.setName(Role.RoleType.USER);
        role.setDescription("app user");
        Role save = repository.save(role);
        System.out.println("save = " + save);
        assertNotNull(save);
    }

    @Test
    @DisplayName("insertAllRoleTest")
    public void test2() {
        Role roleAdmin = new Role(Role.RoleType.ADMIN, "Manage everything");
        Role roleSalesPerson = new Role(Role.RoleType.SALESPERSON, "Manage product price, customer, shipping, orders and sales report");
        Role roleEditor = new Role(Role.RoleType.EDITOR, "Manage categories, brands, products, articles and menus");
        Role roleShipper = new Role(Role.RoleType.SHIPPER, "View products, view orders and update order status");
        Role roleAssistant = new Role(Role.RoleType.ASSISTANT, "Manage questions and reviews");

        List<Role> result = repository.saveAll(List.of(roleAdmin, roleSalesPerson, roleEditor, roleAssistant, roleShipper));
        assertThat(result.size()).isGreaterThan(0);
        //assertTrue(true);
    }
}
