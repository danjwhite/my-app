package com.example.myapp.web.controller;

import com.example.myapp.builder.dto.UserDtoBuilder;
import com.example.myapp.builder.dto.UserPasswordDtoBuilder;
import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.service.RoleService;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import com.example.myapp.test.WebMvcBaseTest;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserControllerTest.UserControllerTestConfig.class})
public class UserControllerTest extends WebMvcBaseTest {

    private static final UserService userServiceMock = EasyMock.strictMock(UserService.class);
    private static final RoleService roleServiceMock = EasyMock.strictMock(RoleService.class);
    private static final SecurityService securityServiceMock = EasyMock.strictMock(SecurityService.class);

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void init() {
        initMocks(userServiceMock, roleServiceMock, securityServiceMock);
    }

    @Before
    public void setUp() {
        resetAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void getUserAccountShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown() throws Exception {
        final String username = "mjones";

        expectFindAllRoles(getRoles());
        expectFindByUsernameThrowsException(username, new AccessDeniedException("Access id denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/view"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void getUserAccountShouldReturn404NotFoundStatusWhenUserNotFound() throws Exception {
        final String username = "mjones";

        expectFindAllRoles(getRoles());
        expectFindByUsernameThrowsException(username, new UsernameNotFoundException("Invalid username"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/view"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getUserAccountShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final User user = newUser();
        final List<Role> roles = getRoles();

        expectFindAllRoles(roles);
        expectFindByUserName(user.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + user.getUsername() + "/view"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void editUserInfoShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown() throws Exception {
        final String username = "mjones";

        expectFindAllRoles(getRoles());
        expectFindByUsernameThrowsException(username, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/edit/info"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void editUserInfoShouldReturn404NotFoundStatusWhenUserNotFound() throws Exception {
        final String username = "mjones";

        expectFindAllRoles(getRoles());
        expectFindByUsernameThrowsException(username, new UsernameNotFoundException("Invalid username"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/edit/info"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editUserInfoShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final List<Role> roles = getRoles();
        final User user = newUser();
        final UserDto userDto = UserDtoBuilder.givenUserDto().withUsername(user.getUsername())
                .withFirstName(user.getFirstName())
                .withLastName(user.getLastName())
                .withRoles(user.getRoles())
                .build();

        expectFindAllRoles(roles);
        expectFindByUserName(user.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + user.getUsername() + "/edit/info"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldNotProceedWhenFirstNameIsNull() throws Exception {
        final String username = "mjones";
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + username + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "firstName", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldNotProceedWhenFirstNameIsBlank() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "firstName", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldNotProceedWhenLastNameIsNull() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "lastName", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldNotProceedWhenLastNameIsBlank() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "lastName", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldNotProceedWhenNoRolesAreSelected() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .build();

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "roles", "Size"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        expectUpdateUserThrowsException(userDto, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldReturn404NotFoundStatusWhenUsernameNotFoundExceptionIsThrown() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        expectUpdateUserThrowsException(userDto, new UsernameNotFoundException("Invalid username"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldReturn404NotFoundStatusWhenEntityNotFoundExceptionIsThrown() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        expectUpdateUserThrowsException(userDto, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldRedirectToAdminPageWhenModeParamIsSetToAdminAndCurrentUserIsAdmin() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        expectUpdateUser(userDto);
        expectAdminRoleCheck(true);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("mode", "admin")
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin?confirmation=edited"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldRedirectToUserPageWhenModeParamIsSetToAdminAndCurrentUserIsNotAdmin() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        expectUpdateUser(userDto);
        expectAdminRoleCheck(false);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("mode", "admin")
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + userDto.getUsername() + "/view?confirmation=infoUpdated"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldRedirectToUserPageWhenModeParamIsNotAdmin() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        expectUpdateUser(userDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("mode", "test")
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + userDto.getUsername() + "/view?confirmation=infoUpdated"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldRedirectToUserPageWhenModeParamIsNull() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectFindAllRoles(getRoles());
        expectUpdateUser(userDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + userDto.getUsername() + "/view?confirmation=infoUpdated"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final String username = "mjones";

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/edit/password"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", Matchers.hasProperty("username", Matchers.is(username))))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", Matchers.hasProperty("password", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", Matchers.hasProperty("newPassword", Matchers.nullValue())))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", Matchers.hasProperty("confirmNewPassword", Matchers.nullValue())));

        verifyAll();

    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenPasswordIsNull() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto(null, "test", "test");

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "password", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenPasswordIsBlank() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("", "test", "test");

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "password", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenNewPasswordIsNull() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", null, "test");

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "newPassword", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenNewPasswordIsBlank() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "", "test");

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "newPassword", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedwhenConfirmNewPasswordIsNull() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", null);

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "confirmNewPassword", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenConfirmNewPasswordIsBlank() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "");

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "confirmNewPassword", "NotBlank"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenPasswordsDoNotMatch() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "doesnotmatch");

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "confirmNewPassword", "FieldMatch"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldReturn403ForbiddenStatusWhenFindByUsernameThrowsAccessDeniedException() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "test");

        expectFindAllRoles(getRoles());
        expectFindByUsernameThrowsException(userPasswordDto.getUsername(), new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldRetrun404NotFoundStatusWhenUsernameNotFoundExceptionIsThrown() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "test");

        expectFindAllRoles(getRoles());
        expectFindByUsernameThrowsException(userPasswordDto.getUsername(), new UsernameNotFoundException("Invalid username"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenCurrentPasswordIsInvalid() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("invalid", "test", "test");
        final User user = newUser();

        Assert.assertFalse(BCrypt.checkpw(userPasswordDto.getPassword(), user.getPassword()));

        expectFindAllRoles(getRoles());
        expectFindByUserName(userPasswordDto.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "password", "InvalidPassword"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldReturn403ForbiddenStatusWhenUpdatePasswordThrowsAccessDeniedException() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "test");
        final User user = newUser();

        expectFindAllRoles(getRoles());
        expectFindByUserName(userPasswordDto.getUsername(), user);
        expectUpdatePasswordThrowsException(userPasswordDto, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldRedirectToExpectedViewWithExpectedAttributes() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "test");
        final User user = newUser();

        expectFindAllRoles(getRoles());
        expectFindByUserName(userPasswordDto.getUsername(), user);
        expectUpdatePassword(userPasswordDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + userPasswordDto.getUsername() + "/view?confirmation=passwordUpdated"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void deleteAccountShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown()  throws Exception {
        final String username = "mjones";

        expectFindAllRoles(getRoles());
        expectDeleteThrowsException(username, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/delete"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void deleteAccountShouldReturn404NotFoundStatusUsernameNotFoundExceptionIsThrown() throws Exception {
        final String username = "mjones";

        expectDeleteThrowsException(username, new UsernameNotFoundException("Invalid username"));
        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/delete"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteAccountShouldRedirectToExpectedView() throws Exception {
        final String username = "mjones";

        expectDeleteUser(username);
        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/delete"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/logout"));

        verifyAll();
    }

    @Test
    public void loginShouldReturnExpectedView() throws Exception {
        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("loginForm"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void logoutShouldLogUserOutOfSecurityContextWhenAuthIsPresent() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertEquals("mjones", auth.getName());

        expectFindAllRoles(getRoles());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?logout"));

        verifyAll();

        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private void expectFindAllRoles(List<Role> roles) {
        EasyMock.expect(roleServiceMock.findAll()).andReturn(roles);
    }

    private <T extends RuntimeException> void expectFindByUsernameThrowsException(String username, T exception) {
        EasyMock.expect(userServiceMock.findByUsername(username)).andThrow(exception);
    }

    private void expectFindByUserName(String username, User user) {
        EasyMock.expect(userServiceMock.findByUsername(username)).andReturn(user);
    }

    private <T extends RuntimeException> void expectUpdateUserThrowsException(UserDto userDto, T exception) {
        EasyMock.expect(userServiceMock.update(userDto)).andThrow(exception);
    }

    private void expectUpdateUser(UserDto user) {
        EasyMock.expect(userServiceMock.update(user))
                .andReturn(new User());
    }

    private void expectAdminRoleCheck(boolean userIsAdmin) {
        EasyMock.expect(securityServiceMock.currentAuthenticationHasRole(RoleType.ROLE_ADMIN))
                .andReturn(userIsAdmin);
    }

    private <T extends RuntimeException> void expectUpdatePasswordThrowsException(UserPasswordDto userPasswordDto, T exception) {
        EasyMock.expect(userServiceMock.updatePassword(userPasswordDto)).andThrow(exception);
    }

    private void expectUpdatePassword(UserPasswordDto userPasswordDto) {
        EasyMock.expect(userServiceMock.updatePassword(userPasswordDto)).andReturn(new User());
    }

    private <T extends RuntimeException> void expectDeleteThrowsException(String username, T exception) {
        userServiceMock.delete(username);
        EasyMock.expectLastCall().andThrow(exception);
    }

    private void expectDeleteUser(String username) {
        userServiceMock.delete(username);
        EasyMock.expectLastCall();
    }

    private Role newRole(long id, RoleType roleType) {
        return RoleBuilder.givenRole().withId(id).withType(roleType).build();
    }

    private List<Role> getRoles() {
        return Arrays.asList(newRole(1L, RoleType.ROLE_USER), newRole(2L, RoleType.ROLE_ADMIN));
    }

    private User newUser() {
        Set<Role> roles = Collections.singleton(newRole(1L, RoleType.ROLE_USER));

        return UserBuilder.givenUser()
                .withId(1L)
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword(new BCryptPasswordEncoder().encode("test123"))
                .withRoles(roles)
                .build();
    }

    private UserPasswordDto newUserPasswordDto(String password, String newPassword, String confirmNewPassword) {
        return UserPasswordDtoBuilder.givenUserPasswordDto()
                .withUsername("mjones")
                .withPassword(password)
                .withNewPassword(newPassword)
                .withConfirmNewPassword(confirmNewPassword)
                .build();
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class UserControllerTestConfig {

        @Bean
        public UserController userController() {
            return new UserController(userServiceMock, roleServiceMock, securityServiceMock);
        }
    }
}
