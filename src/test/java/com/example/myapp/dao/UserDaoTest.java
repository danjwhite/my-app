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
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UserDaoTest {

    @Autowired
    private IUserDao userDao;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional
    @Rollback
    public void countShouldReturnExpectedResult() {
        Assert.assertEquals(2, userDao.count());
    }

    @Test
    @Transactional
    @Rollback
    public void findAllShouldReturnExpectedResult() {
        Assert.assertEquals(2, userDao.findAll().size());
    }

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void findByIdShouldReturnExpectedResult() {
        User user = userDao.findById(1L);

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

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void findByUsernameShouldReturnExpectedResult() {
        User user = userDao.findByUsername("mjones");

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
    public void addShouldSetExpectedFields() {

        User user = new User();

        Role userRole = roleRepository.findByType(RoleType.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        user.setFirstName("Joseph");
        user.setLastName("Manning");
        user.setUsername("jmanning");
        user.setPassword("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS");
        user.setRoles(roles);

        Assert.assertEquals(2, userDao.count());

        User savedUser = userDao.add(user);

        Assert.assertEquals(3, userDao.count());
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
    public void updateShouldSetExpectedFields() {

        // Get user and save original fields.
        User user = userDao.findById(2L);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();
        String password = user.getPassword();
        Set<Role> roles = new HashSet<>(user.getRoles());

        // Declare updated roles.
        Set<Role> updatedRoles = user.getRoles();
        updatedRoles.add(roleRepository.findByType(RoleType.ROLE_ADMIN));

        // Set user roles.
        user.setRoles(updatedRoles);

        Assert.assertEquals(2, userDao.count());

        // Update and retrieve user.
        userDao.update(user);
        User updatedUser = userDao.findById(2L);

        Assert.assertEquals(2, userDao.count());
        Assert.assertEquals(firstName, updatedUser.getFirstName());
        Assert.assertEquals(lastName, updatedUser.getLastName());
        Assert.assertEquals(username, updatedUser.getUsername());
        Assert.assertEquals(password, updatedUser.getPassword());

        Assert.assertEquals(updatedRoles, updatedUser.getRoles());

        Assert.assertNotEquals(roles, updatedUser.getRoles());
    }

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void deleteShouldDeleteExpectedUser() {

        // Get user to delete.
        User user = userDao.findByUsername("mjones");

        Assert.assertEquals(2, userDao.count());
        Assert.assertNotNull(user);

        userDao.delete(user);

        Assert.assertEquals(1, userDao.count());
        Assert.assertNull(userDao.findByUsername("mjones"));
    }
}
