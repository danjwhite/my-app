package com.example.myapp.dao;

import static org.junit.Assert.*;

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

import java.util.*;

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
        assertEquals(4, userDao.count());
    }

    @Test
    @Transactional
    public void testFindAll() {
        assertEquals(4, userDao.findAll().size());
    }

    @Test
    @Transactional
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
        assertEquals("$2y$10$58hikU/OYmVojQkUwGQHRO9.oKPVPG6t3WShvU4NqHNTzzloZpwXC", user.getPassword());
        assertEquals(roles, user.getRoles());
    }

    @Test
    @Transactional
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
        assertEquals("$2y$10$58hikU/OYmVojQkUwGQHRO9.oKPVPG6t3WShvU4NqHNTzzloZpwXC", user.getPassword());
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
        user.setPassword("$2y$10$58hikU/OYmVojQkUwGQHRO9.oKPVPG6t3WShvU4NqHNTzzloZpwXC");
        user.setRoles(roles);

        assertEquals(4, userDao.count());

        User savedUser = userDao.add(user);

        assertEquals(5, userDao.count());
        assertEquals(5L, savedUser.getId().longValue());
        assertEquals("Joseph", savedUser.getFirstName());
        assertEquals("Manning", savedUser.getLastName());
        assertEquals("jmanning", savedUser.getUsername());
        assertEquals("$2y$10$58hikU/OYmVojQkUwGQHRO9.oKPVPG6t3WShvU4NqHNTzzloZpwXC", user.getPassword());
        assertEquals(roles, savedUser.getRoles());
    }

    @Test
    @Transactional
    public void testUpdate() {

        // Get user and save original fields.
        User user = userDao.findById(3L);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();
        String password = user.getPassword();
        Set<Role> roles = new HashSet<>(user.getRoles());

        // Declare updated password.
        String updatedPassword = "$2y$10$um0c/380Ny2oEDo5nS0OXegxVmWyoMd0.gIWyaX6PrseSqIdqinbK";

        // Declare updated roles.
        Set<Role> updatedRoles = user.getRoles();
        updatedRoles.add(roleDao.findByType("ROLE_ADMIN"));

        // Set user password and roles.
        user.setPassword(updatedPassword);
        user.setRoles(updatedRoles);

        assertEquals(4, userDao.count());

        // Update and retrieve user.
        userDao.update(user);
        User updatedUser = userDao.findById(3L);

        assertEquals(4, userDao.count());
        assertEquals(firstName, updatedUser.getFirstName());
        assertEquals(lastName, updatedUser.getLastName());
        assertEquals(username, updatedUser.getUsername());

        assertEquals(updatedPassword, updatedUser.getPassword());
        assertEquals(updatedRoles, updatedUser.getRoles());

        assertNotEquals(password, updatedUser.getPassword());
        assertNotEquals(roles, updatedUser.getRoles());
    }

    @Test
    @Transactional
    public void testDelete() {

        assertEquals(4, userDao.count());
        assertNotNull(userDao.findByUsername("mjones"));

        userDao.delete("mjones");

        assertEquals(3, userDao.count());
        assertNull(userDao.findByUsername("mjones"));
    }
}
