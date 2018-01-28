package com.example.myapp.service;

import static org.junit.Assert.*;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserRegistrationDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @Transactional
    public void testCount() {
        assertEquals(4, userService.count());
    }

    @Test
    @Transactional
    public void testFindAll() {
        assertEquals(4, userService.findAll().size());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testFindById() {
        User user = userService.findById(1L);

        Role userRole = roleService.findByType("ROLE_USER");
        Role adminRole = roleService.findByType("ROLE_ADMIN");
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
    public void testFindByUserName() {
        User user = userService.findByUsername("mjones");

        Role userRole = roleService.findByType("ROLE_USER");
        Role adminRole = roleService.findByType("ROLE_ADMIN");
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

        String firstName = "Joseph";
        String lastName = "Manning";
        String userName = "jmanning";
        String password = "password123";

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByType("ROLE_USER"));

        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setFirstName(firstName);
        userRegistrationDto.setLastName(lastName);
        userRegistrationDto.setUsername(userName);
        userRegistrationDto.setPassword(password);

        assertEquals(4, userService.count());

        User user = userService.add(userRegistrationDto);

        assertEquals(5, userService.count());
        assertEquals(5L, user.getId().longValue());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(userName, user.getUsername());
        assertTrue(bCryptPasswordEncoder.matches(password, user.getPassword()));
        assertEquals(roles, user.getRoles());
    }

    @Test
    @Transactional
    public void testUpdate() {

        // Get user and save original field values.
        User user = userService.findById(3L);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();
        String password = user.getPassword();
        Set<Role> roles = new HashSet<>(user.getRoles());

        // Declare updated roles.
        Set<Role> updatedRoles = user.getRoles();
        updatedRoles.add(roleService.findByType("ROLE_ADMIN"));

        // Set new user roles.
        user.setRoles(updatedRoles);

        // Assert the user count.
        assertEquals(4, userService.count());

        // Update user and retrieve updated user.
        userService.update(user);
        User updatedUser = userService.findById(3L);

        // Assert the user count.
        assertEquals(4, userService.count());

        // Assert that non-updated fields remain the same.
        assertEquals(firstName, updatedUser.getFirstName());
        assertEquals(lastName, updatedUser.getLastName());
        assertEquals(username, updatedUser.getUsername());
        assertEquals(password, updatedUser.getPassword());

        // Assert that user roles were updated.
        assertEquals(updatedRoles, updatedUser.getRoles());

        // Assert that original password and roles do not match the updated ones.
        assertFalse(BCrypt.checkpw(password, updatedUser.getPassword()));
        assertNotEquals(roles, updatedUser.getRoles());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testDelete() {
        assertEquals(4, userService.count());
        assertNotNull(userService.findByUsername("mjones"));

        userService.delete("mjones");

        assertEquals(3, userService.count());
        assertNull(userService.findByUsername("mjones"));
    }
}
