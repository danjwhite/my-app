package com.example.myapp.dao;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDaoTest {

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IRoleDao roleDao;

    @Test
    @Transactional
    public void testCount() {
        assertEquals(2, userDao.count());
    }

    @Test
    @Transactional
    public void testFindAll() {
        assertEquals(2, userDao.findAll().size());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testFindById() {
        User user = userDao.findById(1L);

        Role userRole = roleDao.findByType("ROLE_USER");
        Role adminRole = roleDao.findByType("ROLE_ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        assertEquals(1L, user.getId().longValue());
        assertEquals("Michael", user.getFirstName());
        assertEquals("Jones", user.getLastName());
        assertEquals("mjones", user.getUsername());
        assertEquals("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS", user.getPassword());
        assertEquals(roles, user.getRoles());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testFindByUsername() {
        User user = userDao.findByUsername("mjones");

        Role userRole = roleDao.findByType("ROLE_USER");
        Role adminRole = roleDao.findByType("ROLE_ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        assertEquals(1L, user.getId().longValue());
        assertEquals("Michael", user.getFirstName());
        assertEquals("Jones", user.getLastName());
        assertEquals("mjones", user.getUsername());
        assertEquals("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS", user.getPassword());
        assertEquals(roles, user.getRoles());
    }

    @Test
    @Transactional
    public void testAdd() {

        User user = new User();

        Role userRole = roleDao.findByType("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        user.setFirstName("Joseph");
        user.setLastName("Manning");
        user.setUsername("jmanning");
        user.setPassword("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS");
        user.setRoles(roles);

        assertEquals(2, userDao.count());

        User savedUser = userDao.add(user);

        assertEquals(3, userDao.count());
        assertEquals(3L, savedUser.getId().longValue());
        assertEquals("Joseph", savedUser.getFirstName());
        assertEquals("Manning", savedUser.getLastName());
        assertEquals("jmanning", savedUser.getUsername());
        assertEquals("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS", user.getPassword());
        assertEquals(roles, savedUser.getRoles());
    }

    @Test
    @Transactional
    public void testUpdate() {

        // Get user and save original fields.
        User user = userDao.findById(2L);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();
        String password = user.getPassword();
        Set<Role> roles = new HashSet<>(user.getRoles());

        // Declare updated roles.
        Set<Role> updatedRoles = user.getRoles();
        updatedRoles.add(roleDao.findByType("ROLE_ADMIN"));

        // Set user roles.
        user.setRoles(updatedRoles);

        assertEquals(2, userDao.count());

        // Update and retrieve user.
        userDao.update(user);
        User updatedUser = userDao.findById(2L);

        assertEquals(2, userDao.count());
        assertEquals(firstName, updatedUser.getFirstName());
        assertEquals(lastName, updatedUser.getLastName());
        assertEquals(username, updatedUser.getUsername());
        assertEquals(password, updatedUser.getPassword());

        assertEquals(updatedRoles, updatedUser.getRoles());

        assertNotEquals(roles, updatedUser.getRoles());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testDelete() {

        // Get user to delete.
        User user = userDao.findByUsername("mjones");

        assertEquals(2, userDao.count());
        assertNotNull(user);

        userDao.delete(user);

        assertEquals(1, userDao.count());
        assertNull(userDao.findByUsername("mjones"));
    }
}
