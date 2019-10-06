package com.example.myapp.web.controller;

import com.example.myapp.builder.dto.UserRegistrationDtoBuilder;
import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.RoleService;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import com.example.myapp.test.WebMvcBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebMvcTest(UserRegistrationController.class)
@ContextConfiguration(classes = {UserRegistrationControllerTest.UserRegistrationControllerTestConfig.class})
public class UserRegistrationControllerTest extends WebMvcBaseTest {

    @Autowired
    private MockMvc mockMvc;

    private static UserService userServiceMock;
    private static RoleService roleServiceMock;
    private static SecurityService securityServiceMock;

    @BeforeClass
    public static void init() {
        userServiceMock = EasyMock.strictMock(UserService.class);
        roleServiceMock = EasyMock.strictMock(RoleService.class);
        securityServiceMock = EasyMock.strictMock(SecurityService.class);
        initMocks(userServiceMock, roleServiceMock, securityServiceMock);
    }

    @Before
    public void setUp() {
        resetAll();
    }

    // TODO: Rename
    @Test
    public void showRegistrationFormShouldReturn404NotFoundStatusWhenEntityNotFoundExceptionIsThrown() throws Exception {
        expectFindAllRoles(getRoles());
        expectFindRoleByTypeThrowsEntityNotFoundException(RoleType.ROLE_USER);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    public void showRegistrationFormShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);

        expectFindAllRoles(getRoles());
        expectFindRoleByType(userRole.getType(), userRole);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenFirstNameIsNull() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "firstName", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenFirstNameIsBlank() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName(StringUtils.EMPTY)
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.isEmptyString())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "firstName", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenLastNameIsNull() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "lastName", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenLastNameIsBlank() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName(StringUtils.EMPTY)
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.isEmptyString())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "lastName", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenUsernameIsNull() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "username", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenUsernameIsBlank() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername(StringUtils.EMPTY)
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.isEmptyString())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "username", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenPasswordIsNull() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "password", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenPasswordIsBlank() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword(StringUtils.EMPTY)
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.isEmptyString())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "password", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenConfirmPasswordIsNull() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "confirmPassword", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenConfirmPasswordIsBlank() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword(StringUtils.EMPTY)
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.isEmptyString())))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "confirmPassword", "NotBlank"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenPasswordsDoNotMatch() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test")
                .withRoles(Collections.singleton(userRole))
                .build();

        Assert.assertNotEquals(registrationDto.getPassword(), registrationDto.getConfirmPassword());

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "confirmPassword", "FieldMatch"));

        verifyAll();
    }


    @Test
    public void registerUserShouldNotProceedWhenNoRolesSelected() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.empty())))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "roles", "Size"));

        verifyAll();
    }

    @Test
    public void registerUserShouldNotProceedWhenUsernameAlreadyExists() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        expectUserExistsCheck(registrationDto.getUsername(), true);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("firstName", Matchers.is(registrationDto.getFirstName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("lastName", Matchers.is(registrationDto.getLastName()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("username", Matchers.is(registrationDto.getUsername()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("password", Matchers.is(registrationDto.getPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("confirmPassword", Matchers.is(registrationDto.getConfirmPassword()))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.hasSize(1))))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.hasProperty("roles", Matchers.contains(userRole))))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "username", "UsernameAlreadyTaken"));


        verifyAll();
    }

    // TODO: Rename
    @Test
    public void registerUserShouldReturn404NotFoundStatusWhenAddingUserThrowsEntityNotFoundException() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        expectUserExistsCheck(registrationDto.getUsername(), false);
        expectAddUserThrowsEntityNotFoundException(registrationDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));


        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones", roles = {"USER", "ADMIN"})
    public void registerUserShouldRedirectToAdminViewWhenCurrentUserIsAdmin() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        expectUserExistsCheck(registrationDto.getUsername(), false);
        expectAddUser(registrationDto);
        expectAdminRoleCheck(true);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin?confirmation=created"));


        verifyAll();
    }

    @Test
    public void registerUserShouldAutoLoginUserAndRedirectToUserViewWhenCurrentUserIsNotAdmin() throws Exception {
        final Role userRole = newRole(1L, RoleType.ROLE_USER);
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles(getRoles());
        expectUserExistsCheck(registrationDto.getUsername(), false);
        expectAddUser(registrationDto);
        expectAdminRoleCheck(false);
        expectAutoLogin(registrationDto.getUsername(), registrationDto.getPassword());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + registrationDto.getUsername() + "/view?confirmation=created"));


        verifyAll();
    }

    private void expectFindAllRoles(List<Role> roles) {
        EasyMock.expect(roleServiceMock.findAll()).andReturn(roles);
    }

    private void expectFindRoleByTypeThrowsEntityNotFoundException(RoleType roleType) {
        EasyMock.expect(roleServiceMock.findByType(roleType))
                .andThrow(new EntityNotFoundException());
    }

    private void expectFindRoleByType(RoleType roleType, Role role) {
        EasyMock.expect(roleServiceMock.findByType(roleType)).andReturn(role);
    }

    private void expectUserExistsCheck(String username, boolean userExists) {
        EasyMock.expect(userServiceMock.userExists(username)).andReturn(userExists);
    }

    private void expectAddUserThrowsEntityNotFoundException(UserRegistrationDto userRegistrationDto) {
        EasyMock.expect(userServiceMock.add(userRegistrationDto)).andThrow(new EntityNotFoundException());
    }

    private void expectAddUser(UserRegistrationDto userRegistrationDto) {
        EasyMock.expect(userServiceMock.add(userRegistrationDto)).andReturn(new User());
    }

    private void expectAdminRoleCheck(boolean userIsAdmin) {
        EasyMock.expect(securityServiceMock.currentAuthenticationHasRole(RoleType.ROLE_ADMIN))
                .andReturn(userIsAdmin);
    }

    private void expectAutoLogin(String username, String password) {
        securityServiceMock.autoLogin(username, password);
        EasyMock.expectLastCall();
    }

    private Role newRole(long id, RoleType roleType) {
        return RoleBuilder.givenRole().withId(id).withType(roleType).build();
    }

    private List<Role> getRoles() {
        return Arrays.asList(newRole(1L, RoleType.ROLE_USER), newRole(2L, RoleType.ROLE_ADMIN));
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class UserRegistrationControllerTestConfig {

        @Bean
        public UserRegistrationController userRegistrationController() {
            return new UserRegistrationController(userServiceMock, roleServiceMock, securityServiceMock);
        }
    }
}
