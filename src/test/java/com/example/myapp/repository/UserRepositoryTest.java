package com.example.myapp.repository;

import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional
    @Rollback
    public void countShouldReturnExpectedResult() {
        newUser("user1");
        newUser("user2");

        Assert.assertEquals(2, userRepository.count());
    }

    @Test
    @Transactional
    @Rollback
    public void findAllShouldReturnExpectedResult() {
        List<User> users = Arrays.asList(newUser("user1"), newUser("user2"));

        List<User> result = (List<User>) userRepository.findAll();
        Assert.assertEquals(users, result);
    }

    @Test
    @Transactional
    @Rollback
    public void findByIdShouldReturnExpectedResult() {
        User user1 = newUser("user");
        newUser("user2");

        Optional<User> result = userRepository.findById(user1.getId());
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(user1, result.get());
    }

    @Test
    @Transactional
    @Rollback
    public void findByUsernameShouldReturnExpectedResult() {
        User user1 = newUser("user1");
        newUser("user2");

        User result = userRepository.findByUsername("user1");
        Assert.assertEquals(user1, result);
    }

    @Test
    @Rollback
    @Transactional
    public void saveShouldSetExpectedFieldsForAdd() {
        final String firstName = "Mike";
        final String lastName = "Jones";
        final String username = "mjones";
        final Set<Role> roles = Collections.singleton(roleRepository.findByType(RoleType.ROLE_USER));

        User user = UserBuilder.givenUser().withFirstName(firstName)
                .withLastName(lastName)
                .withUsername(username)
                .withPassword("test123")
                .withRoles(roles)
                .build();

        userRepository.save(user);
        Assert.assertNotNull(user.getId());

        Optional<User> savedUser = userRepository.findById(user.getId());
        Assert.assertTrue(savedUser.isPresent());
        Assert.assertEquals(firstName, savedUser.get().getFirstName());
        Assert.assertEquals(lastName, savedUser.get().getLastName());
        Assert.assertEquals(username, savedUser.get().getUsername());
        Assert.assertEquals(roles, savedUser.get().getRoles());
    }

    @Test
    @Transactional
    @Rollback
    public void deleteShouldDeleteExpectedUser() {
        User user = newUser("mjones");
        Assert.assertTrue(userRepository.findById(user.getId()).isPresent());

        userRepository.delete(user);
        Assert.assertFalse(userRepository.findById(user.getId()).isPresent());
    }

    private User newUser(String username) {
        return UserBuilder.givenUser(entityManager).withFirstName("Test")
                .withLastName("User")
                .withUsername(username)
                .withPassword("test123")
                .withRoles(Collections.singleton(roleRepository.findByType(RoleType.ROLE_USER)))
                .build();
    }
}
