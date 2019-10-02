package com.example.myapp.service;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserRegistrationDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @Transactional
    public void testCount() {
        assertEquals(2, userService.count());
    }

    @Test
    @Transactional
    public void testFindAll() {
        assertEquals(2, userService.findAll().size());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testFindById() {
        User user = userService.findById(1L);

        Role userRole = roleService.findByType(RoleType.ROLE_USER);
        Role adminRole = roleService.findByType(RoleType.ROLE_ADMIN);
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
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testFindByUserName() {
        User user = userService.findByUsername("mjones");

        Role userRole = roleService.findByType(RoleType.ROLE_USER);
        Role adminRole = roleService.findByType(RoleType.ROLE_ADMIN);
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
        roles.add(roleService.findByType(RoleType.ROLE_USER));

        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setFirstName(firstName);
        userRegistrationDto.setLastName(lastName);
        userRegistrationDto.setUsername(userName);
        userRegistrationDto.setPassword(password);

        assertEquals(2, userService.count());

        User user = userService.add(userRegistrationDto);

        assertEquals(3, userService.count());
        assertEquals(3L, user.getId().longValue());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(userName, user.getUsername());
        assertTrue(bCryptPasswordEncoder.matches(password, user.getPassword()));
        assertEquals(roles, user.getRoles());
    }

    @Test
    @Transactional
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testUpdate() {

        // Get user and save original field values.
        User user = userService.findById(1L);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();
        String password = user.getPassword();
        Set<Role> roles = new HashSet<>(user.getRoles());

        // Create UserDto from user and set first name.
        UserDto userDto = new UserDto(user);
        userDto.setFirstName("Mike");

        // Assert the user count.
        assertEquals(2, userService.count());

        // Update user and retrieve updated user.
        userService.update(userDto);
        User updatedUser = userService.findById(1L);

        // Assert the user count.
        assertEquals(2, userService.count());

        // Assert that the updated field is different.
        assertNotEquals(firstName, updatedUser.getFirstName());
        assertEquals("Mike", updatedUser.getFirstName());

        // Assert that non-updated fields remain the same.
        assertEquals(lastName, updatedUser.getLastName());
        assertEquals(username, updatedUser.getUsername());
        assertEquals(password, updatedUser.getPassword());
        assertEquals(roles, updatedUser.getRoles());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testDelete() {

        // Get user to delete.
        User user = userService.findByUsername("mjones");

        assertEquals(2, userService.count());
        assertNotNull(user);

        userService.delete(user);

        assertEquals(1, userService.count());
        assertNull(userService.findByUsername("mjones"));
    }
}
