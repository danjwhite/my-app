package com.example.myapp.service;

import com.example.myapp.builder.dto.UserDtoBuilder;
import com.example.myapp.builder.dto.UserPasswordDtoBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.repository.RoleRepository;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class UserServiceSecurityIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones")
    public void findByUsernameShouldThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsernameAndUserIsNotAdmin() {
        expectAccessDeniedException();
        userService.findByUsername("test");
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones")
    public void findByUsernameShouldNotThrowExceptionWhenUsernameMatchesAuthenticatedUsernameAndUserIsNotAdmin() {
        userService.findByUsername(newUser("mjones").getUsername());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones", roles = {"USER", "ADMIN"})
    public void findByUsernameShouldNotThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsernameAndUserIsAdmin() {
        userService.findByUsername(newUser("test").getUsername());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones")
    public void findAllShouldThrowExceptionWhenUserIsNotAdmin() {
        expectAccessDeniedException();
        userService.findAll();
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones", roles = {"USER", "ADMIN"})
    public void findAllShouldNotThrowExceptionWhenUserIsAdmin() {
        userService.findAll();
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones")
    public void updateShouldThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsernameAndUserIsNotAdmin() {
        expectAccessDeniedException();
        userService.update(newUserDto("test"));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones")
    public void updateShouldNotThrowExceptionWhenUsernameMatchesAuthenticatedUsernameAndUserIsNotAdmin() {
        userService.update(newUserDto("mjones"));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones", roles = {"USER", "ADMIN"})
    public void updateShouldNotThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsernameAndUserIsAdmin() {
        userService.update(newUserDto("test"));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones", roles = {"USER", "ADMIN"})
    public void updatePasswordShouldThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsername() {
        expectAccessDeniedException();
        userService.updatePassword(newUserPasswordDto("test"));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldNotThrowExceptionWhenUsernameMatchesAuthenticatedUsername() {
        userService.updatePassword(newUserPasswordDto("mjones"));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones")
    public void deleteShouldThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsernameAndUserIsNotAdmin() {
        expectAccessDeniedException();
        userService.delete(newUser("test").getUsername());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones")
    public void deleteShouldNotThrowExceptionWhenUsernameMatchesAuthenticatedUsernameAndUserIsNotAdmin() {
        userService.delete(newUser("mjones").getUsername());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "mjones", roles = {"USER", "ADMIN"})
    public void deleteShouldNotThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsernameAndUserIsAdmin() {
        userService.delete(newUser("test").getUsername());
    }

    private void expectAccessDeniedException() {
        expectedException.expect(AccessDeniedException.class);
        expectedException.expectMessage("Access is denied");
    }

    private User newUser(String username) {
        return UserBuilder.givenUser(entityManager).withFirstName("Mike")
                .withLastName("Jones")
                .withUsername(username)
                .withPassword("test123")
                .withRoles(Collections.singleton(roleRepository.findByType(RoleType.ROLE_USER)))
                .build();
    }

    private UserDto newUserDto(String username) {
        User user = newUser(username);

        return UserDtoBuilder.givenUserDto().withFirstName("Test").withLastName("User")
                .withUsername(username)
                .withRoles(user.getRoles())
                .build();

    }

    private UserPasswordDto newUserPasswordDto(String username) {
        User user = newUser(username);

        return UserPasswordDtoBuilder.givenUserPasswordDto().withUsername(username)
                .withPassword(user.getPassword())
                .withNewPassword("test")
                .withConfirmPassword("test")
                .build();
    }
}
