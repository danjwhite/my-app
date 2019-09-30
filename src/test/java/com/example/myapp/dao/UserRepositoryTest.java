package com.example.myapp.dao;

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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional
    @Rollback
    public void countShouldReturnExpectedResult() {
        Assert.assertEquals(2, userRepository.count());
    }

    @Test
    @Transactional
    @Rollback
    public void findAllShouldReturnExpectedResult() {
        Assert.assertEquals(2, ((List<User>) userRepository.findAll()).size());
    }

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void findByIdShouldReturnExpectedResult() {
        Optional<User> user = userRepository.findById(1L);

        Role userRole = roleRepository.findByType(RoleType.ROLE_USER);
        Role adminRole = roleRepository.findByType(RoleType.ROLE_ADMIN);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        Assert.assertTrue(user.isPresent());
        Assert.assertEquals(1L, user.get().getId().longValue());
        Assert.assertEquals("Michael", user.get().getFirstName());
        Assert.assertEquals("Jones", user.get().getLastName());
        Assert.assertEquals("mjones", user.get().getUsername());
        Assert.assertEquals("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS", user.get().getPassword());
        Assert.assertEquals(roles, user.get().getRoles());
    }

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void findByUsernameShouldReturnExpectedResult() {
        User user = userRepository.findByUsername("mjones");

        Role userRole = roleRepository.findByType(RoleType.ROLE_USER);
        Role adminRole = roleRepository.findByType(RoleType.ROLE_ADMIN);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        Assert.assertEquals(1L, user.getId().longValue());
        Assert.assertEquals("Michael", user.getFirstName());
        Assert.assertEquals("Jones", user.getLastName());
        Assert.assertEquals("mjones", user.getUsername());
        Assert.assertEquals("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS", user.getPassword());
        Assert.assertEquals(roles, user.getRoles());
    }

    // TODO: Break down into smaller tests
    @Test
    @Rollback
    @Transactional
    public void saveShouldSetExpectedFieldsforAdd() {

        User user = new User();

        Role userRole = roleRepository.findByType(RoleType.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        user.setFirstName("Joseph");
        user.setLastName("Manning");
        user.setUsername("jmanning");
        user.setPassword("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS");
        user.setRoles(roles);

        Assert.assertEquals(2, userRepository.count());

        User savedUser = userRepository.save(user);

        Assert.assertEquals(3, userRepository.count());
        Assert.assertEquals(3L, savedUser.getId().longValue());
        Assert.assertEquals("Joseph", savedUser.getFirstName());
        Assert.assertEquals("Manning", savedUser.getLastName());
        Assert.assertEquals("jmanning", savedUser.getUsername());
        Assert.assertEquals("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS", user.getPassword());
        Assert.assertEquals(roles, savedUser.getRoles());
    }

    // TODO: Break down into smaller tests
    @Test
    @Transactional
    @Rollback
    public void saveShouldSetExpectedFieldsForUpdate() {

        // Get user and save original fields.
        Optional<User> user = userRepository.findById(2L);
        Assert.assertTrue(user.isPresent());
        String firstName = user.get().getFirstName();
        String lastName = user.get().getLastName();
        String username = user.get().getUsername();
        String password = user.get().getPassword();
        Set<Role> roles = new HashSet<>(user.get().getRoles());

        // Declare updated roles.
        Set<Role> updatedRoles = user.get().getRoles();
        updatedRoles.add(roleRepository.findByType(RoleType.ROLE_ADMIN));

        // Set user roles.
        user.get().setRoles(updatedRoles);

        Assert.assertEquals(2, userRepository.count());

        // Update and retrieve user.
        userRepository.save(user.get());
        Optional<User> updatedUser = userRepository.findById(2L);

        Assert.assertTrue(updatedUser.isPresent());
        Assert.assertEquals(2, userRepository.count());
        Assert.assertEquals(firstName, updatedUser.get().getFirstName());
        Assert.assertEquals(lastName, updatedUser.get().getLastName());
        Assert.assertEquals(username, updatedUser.get().getUsername());
        Assert.assertEquals(password, updatedUser.get().getPassword());

        Assert.assertEquals(updatedRoles, updatedUser.get().getRoles());

        Assert.assertNotEquals(roles, updatedUser.get().getRoles());
    }

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void deleteShouldDeleteExpectedUser() {

        // Get user to delete.
        User user = userRepository.findByUsername("mjones");

        Assert.assertEquals(2, userRepository.count());
        Assert.assertNotNull(user);

        userRepository.delete(user);

        Assert.assertEquals(1, userRepository.count());
        Assert.assertNull(userRepository.findByUsername("mjones"));
    }
}
