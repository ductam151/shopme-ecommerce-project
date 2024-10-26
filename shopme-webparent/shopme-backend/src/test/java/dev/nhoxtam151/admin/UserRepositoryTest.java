package dev.nhoxtam151.admin;

import dev.nhoxtam151.admin.models.User;
import dev.nhoxtam151.admin.repositories.UserRepository;
import dev.nhoxtam151.shopmecommon.models.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("createUserTest")
    public void test1() {
        User user = new User("tam02@gmail.com",
                "nhoxtam151",
                "test01",
                "default_password");
        //Role role = entityManager.find(Role.class, 1);
        Role role = new Role(1L);
        user.addRole(role);
        User save = userRepository.save(user);
        System.out.println("save = " + save);
        assertNotNull(save);
    }

    @Test
    @DisplayName("addRoleForExistUserTest")
    public void test2() {
        User user = userRepository.findByEmail("test01@gmail.com").orElse(null);
        Role role = entityManager.find(Role.class, 4);
        user.addRole(role);
        User save = userRepository.save(user);
        System.out.println("save = " + save);
        assertThat(save.getRoles().size()).isGreaterThan(1);
    }

    @Test
    @DisplayName("createUserWithTwoRoles")
    public void test3() {
        User userRavi = new User("ravi@gmail.com", "Ravi", "Kumar", "ravi2024");
        Role roleAdmin = entityManager.find(Role.class, 1);
        Role roleAssistant = entityManager.find(Role.class, 4);
        userRavi.addRole(roleAdmin);
        userRavi.addRole(roleAssistant);
        User save = userRepository.save(userRavi);
        System.out.println("save = " + save);
        assertThat(save.getRoles().size()).isGreaterThan(1);
    }

    @Test
    @DisplayName("retrieveAllUsersTest")
    public void test4() {
        List<User> usersList = userRepository.findAll();
        usersList.forEach(System.out::println);
        assertThat(usersList.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("retrieveUserById")
    public void test5() {
        User user = userRepository.findById(3L).orElse(null);
        System.out.println("user = " + user);
        assertNotNull(user);
    }

    @Test
    @DisplayName("updateUserInfoTest")
    public void test6() {
        User user03 = userRepository.findById(3L).orElse(null);
        user03.setEmail("anglejavaprogrammer@gmail.com");
        user03.setFirstName("Angle");
        user03.setLastName("Kumar");
        User save = userRepository.save(user03);
        System.out.println("save = " + save);
        assertThat(save.getFirstName()).isEqualTo("Angle");
    }

    @Test
    @DisplayName("updateUserRolesTest")
    public void test7() {
        User user = userRepository.findById(6L).orElse(null);
        List<Role> roles = user.getRoles();
        roles.remove(new Role(5L));
//        roles.remove(new Role(2L));
//        roles.remove(new Role(4L));
        roles.add(new Role(1L));
        userRepository.save(user);
        System.out.println("save = " + user);
    }

    @Test
    @DisplayName("deleteUserTest")
    public void test8() {
        userRepository.countById(100L);
    }

    @Test
    @DisplayName("paginationUser")
    public void test9() {
        Pageable pageable = PageRequest.of(0, 3);
        Page<User> users = userRepository.findAll(pageable);
        users.forEach(System.out::println);
        long totalElements = users.getTotalElements();
        assertThat(totalElements).isGreaterThan(0);
    }

    @Test
    @DisplayName("paginationUserWithSortByEmailDesc")
    public void test10() {
        String sortField = "email";
        Sort sort = Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(0, 3, sort);
        Page<User> users = userRepository.findAll(pageable);
        long totalElements = users.getTotalElements();
        assertThat(totalElements).isGreaterThan(0);
        users.getContent().forEach(System.out::println);
    }

    @Test
    @DisplayName("findAllByKeyword")
    public void test11() {
        String keyword = "Bruce";
        String sortField = "firstName";
        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(0, 3, sort);
        Page<User> userPage = userRepository.findAll(keyword, pageable);
        userPage.forEach(System.out::println);
        assertThat(userPage.getTotalElements()).isGreaterThan(0);
    }
}
