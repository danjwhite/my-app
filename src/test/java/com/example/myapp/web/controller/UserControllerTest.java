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
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
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
    
    private static final List<Role> roles = new ArrayList<>();
    
    static {
        roles.add(RoleBuilder.givenRole().withId(1L).withType(RoleType.ROLE_USER).build());
        roles.add(RoleBuilder.givenRole().withId(2L).withType(RoleType.ROLE_ADMIN).build());
    }

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession mockHttpSession;
    private User loggedInUser;

    @BeforeClass
    public static void init() {
        initMocks(userServiceMock, roleServiceMock, securityServiceMock);
    }

    @Before
    public void setUp() {
        mockHttpSession = new MockHttpSession();

        loggedInUser = UserBuilder.givenUser()
                .withId(1L)
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword(new BCryptPasswordEncoder().encode("test123"))
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getUserAccountShouldRedirectTo403ForbiddenErrorPageWhenFindByUsernameThrowsAccessDeniedException() throws Exception {
        final String username = "mjones";

        expectGetLoggedInUser();
        expectFindByUsernameThrowsException(username, new AccessDeniedException("Access id denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/view").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getUserAccountShouldRedirectTo404NotFoundErrorPageWhenFindByUsernameThrowsUsernameNotFoundException() throws Exception {
        final String username = "mjones";

        expectGetLoggedInUser();
        expectFindByUsernameThrowsException(username, new UsernameNotFoundException("Invalid username"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/view").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getUserAccountShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final User user = newUser();

        expectGetLoggedInUser();
        expectFindByUserName(user.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + user.getUsername() + "/view").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editUserInfoShouldRedirectTo403ForbiddenErrorPageWhenFindByUsernameThrowsAccessDeniedException() throws Exception {
        final String username = "mjones";

        expectGetLoggedInUser();
        expectFindByUsernameThrowsException(username, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/edit/info").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editUserInfoShouldRedirectTo404NotFoundErrorPageWhenFindByUsernameThrowsUsernameNotFoundException() throws Exception {
        final String username = "mjones";

        expectGetLoggedInUser();
        expectFindByUsernameThrowsException(username, new UsernameNotFoundException("Invalid username"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/edit/info").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editUserInfoShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final User user = newUser();
        final UserDto userDto = UserDtoBuilder.givenUserDto().withUsername(user.getUsername())
                .withFirstName(user.getFirstName())
                .withLastName(user.getLastName())
                .withRoles(user.getRoles())
                .build();

        expectGetLoggedInUser();
        expectFindByUserName(user.getUsername(), user);
        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + user.getUsername() + "/edit/info").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "user", "allRoles"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
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

        expectGetLoggedInUser();
        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + username + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "user", "allRoles"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "firstName", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldNotProceedWhenFirstNameIsBlank() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName(StringUtils.EMPTY)
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectGetLoggedInUser();
        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "user", "allRoles"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "firstName", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldNotProceedWhenLastNameIsNull() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectGetLoggedInUser();
        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "user", "allRoles"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "lastName", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldNotProceedWhenLastNameIsBlank() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName(StringUtils.EMPTY)
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectGetLoggedInUser();
        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "user", "allRoles"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "lastName", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldNotProceedWhenNoRolesAreSelected() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .build();

        expectGetLoggedInUser();
        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "user", "allRoles"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "roles", "Size"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldRedirectTo403ForbiddenErrorPageWhenUpdateUserThrowsAccessDeniedException() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectGetLoggedInUser();
        expectUpdateUserThrowsException(userDto, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldRedirectTo404NotFoundErrorPageWhenUpdateUserThrowsUsernameNotFoundException() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectGetLoggedInUser();
        expectUpdateUserThrowsException(userDto, new UsernameNotFoundException("Invalid username"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserInfoShouldRedirectTo404NotFoundErrorPageWhenUpdateUserThrowsEntityNotFoundException() throws Exception {
        final UserDto userDto = UserDtoBuilder.givenUserDto()
                .withUsername("mjones")
                .withFirstName("Mike")
                .withLastName("Jones")
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();

        expectGetLoggedInUser();
        expectUpdateUserThrowsException(userDto, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
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

        expectGetLoggedInUser();
        expectUpdateUser(userDto);
        expectAdminRoleCheck(true);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("mode", "admin")
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin?confirmation=edited"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
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

        expectGetLoggedInUser();
        expectUpdateUser(userDto);
        expectAdminRoleCheck(false);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("mode", "admin")
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + userDto.getUsername() + "/view?confirmation=infoUpdated"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
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

        expectGetLoggedInUser();
        expectUpdateUser(userDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("mode", "test")
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + userDto.getUsername() + "/view?confirmation=infoUpdated"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
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

        expectGetLoggedInUser();
        expectUpdateUser(userDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userDto.getUsername() + "/edit/info").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", userDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + userDto.getUsername() + "/view?confirmation=infoUpdated"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final UserPasswordDto userPasswordDto = UserPasswordDtoBuilder.givenUserPasswordDto()
                .withUsername("mjones").build();

        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", userPasswordDto));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenPasswordIsNull() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto(null, "test", "test");

        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "password", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenPasswordIsBlank() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto(StringUtils.EMPTY, "test", "test");

        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "password", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenNewPasswordIsNull() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", null, "test");

        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "newPassword", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenNewPasswordIsBlank() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", StringUtils.EMPTY, "test");

        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "newPassword", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedwhenConfirmNewPasswordIsNull() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", null);

        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "confirmPassword", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenConfirmNewPasswordIsBlank() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", StringUtils.EMPTY);

        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "confirmPassword", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenPasswordsDoNotMatch() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "doesnotmatch");

        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "confirmPassword", "FieldMatch"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldRedirectTo403ForbiddenErrorPageWhenFindByUsernameThrowsAccessDeniedException() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "test");

        expectGetLoggedInUser();
        expectFindByUsernameThrowsException(userPasswordDto.getUsername(), new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldRedirectTo404NotFoundErrorPageWhenfindByUsernameThrowsUsernameNotFoundException() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "test");

        expectGetLoggedInUser();
        expectFindByUsernameThrowsException(userPasswordDto.getUsername(), new UsernameNotFoundException("Invalid username"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldNotProceedWhenCurrentPasswordIsInvalid() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("invalid", "test", "test");
        final User user = newUser();

        Assert.assertFalse(BCrypt.checkpw(userPasswordDto.getPassword(), user.getPassword()));

        expectGetLoggedInUser();
        expectFindByUserName(userPasswordDto.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("userPasswordDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("userPasswordDto", "password", "InvalidPassword"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldRedirectTo403ForbiddenErrorPageWhenUpdatePasswordThrowsAccessDeniedException() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "test");
        final User user = newUser();

        expectGetLoggedInUser();
        expectFindByUserName(userPasswordDto.getUsername(), user);
        expectUpdatePasswordThrowsException(userPasswordDto, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editPasswordShouldRedirectToExpectedView() throws Exception {
        final UserPasswordDto userPasswordDto = newUserPasswordDto("test123", "test", "test");
        final User user = newUser();

        expectGetLoggedInUser();
        expectFindByUserName(userPasswordDto.getUsername(), user);
        expectUpdatePassword(userPasswordDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userPasswordDto.getUsername() + "/edit/password").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userPasswordDto", userPasswordDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + userPasswordDto.getUsername() + "/view?confirmation=passwordUpdated"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteAccountShouldRedirectTo403ForbiddenErrorPageWhenDeleteThrowsAccessDeniedException()  throws Exception {
        final String username = "mjones";

        expectGetLoggedInUser();
        expectDeleteThrowsException(username, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/delete").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteAccountShouldRedirectTo404NotFoundErrorPageWhenDeleteThrowsUsernameNotFoundException() throws Exception {
        final String username = "mjones";

        expectGetLoggedInUser();
        expectDeleteThrowsException(username, new UsernameNotFoundException("Invalid username"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/delete").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteAccountShouldRedirectToExpectedView() throws Exception {
        final String username = "mjones";

        expectGetLoggedInUser();
        expectDeleteUser(username);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username + "/delete").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/logout"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    private void expectGetLoggedInUser() {
        EasyMock.expect(userServiceMock.getLoggedInUser())
                .andReturn(loggedInUser);
    }

    private void expectFindAllRoles() {
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
                .withConfirmPassword(confirmNewPassword)
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
