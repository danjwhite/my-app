package com.example.myapp.web.controller;

import com.example.myapp.builder.dto.UserRegistrationDtoBuilder;
import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebMvcTest(UserRegistrationController.class)
@ContextConfiguration(classes = {UserRegistrationControllerTest.UserRegistrationControllerTestConfig.class})
public class UserRegistrationControllerTest extends WebMvcBaseTest {

    private static final UserService userServiceMock = EasyMock.strictMock(UserService.class);
    private static final RoleService roleServiceMock = EasyMock.strictMock(RoleService.class);
    private static final SecurityService securityServiceMock = EasyMock.strictMock(SecurityService.class);

    private static final Role userRole = newRole(1L, RoleType.ROLE_USER);
    private static final List<Role> roles = new ArrayList<>();

    static {
        roles.add(userRole);
        roles.add(newRole(2L, RoleType.ROLE_ADMIN));
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
    public void showRegistrationFormShouldRedirectTo404NotFoundErrorPageWhenFindRoleByTypeThrowsEntityNotFoundException() throws Exception {
        expectFindAllRoles();
        expectFindRoleByTypeThrowsEntityNotFoundException();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/register").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void showRegistrationFormShouldReturnExpectedViewWithExpectedAttributesWhenAuthIsAnonymous() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        expectFindRoleByType();
        expectAnonymousAuthCheck(true);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/register").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("userInContext"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void showRegistrationFormShouldReturnExpectedViewWithExpectedAttributesWhenAuthIsNotAnonymous() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        expectFindRoleByType();
        expectAnonymousAuthCheck(false);
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/register").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("userInContext"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenFirstNameIsNull() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "firstName", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenFirstNameIsBlank() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName(StringUtils.EMPTY)
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "firstName", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenLastNameIsNull() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "lastName", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenLastNameIsBlank() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName(StringUtils.EMPTY)
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "lastName", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenUsernameIsNull() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "username", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenUsernameIsBlank() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername(StringUtils.EMPTY)
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "username", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenPasswordIsNull() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "password", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenPasswordIsBlank() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword(StringUtils.EMPTY)
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "password", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenConfirmPasswordIsNull() throws Exception {
         UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "confirmPassword", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenConfirmPasswordIsBlank() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword(StringUtils.EMPTY)
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "confirmPassword", "NotBlank"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenPasswordsDoNotMatch() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test")
                .withRoles(Collections.singleton(userRole))
                .build();

        Assert.assertNotEquals(registrationDto.getPassword(), registrationDto.getConfirmPassword());

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "confirmPassword", "FieldMatch"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
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

        expectFindAllRoles();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "roles", "Size"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldNotProceedWhenUsernameAlreadyExists() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        expectUserExistsCheck(registrationDto.getUsername(), true);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "username", "UsernameAlreadyTaken"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldRedirectTo404NotFoundErrorPageWhenAddUserThrowsEntityNotFoundException() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        expectUserExistsCheck(registrationDto.getUsername(), false);
        expectAddUserThrowsEntityNotFoundException(registrationDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones", roles = {"USER", "ADMIN"})
    public void registerUserShouldRedirectToAdminViewWhenCurrentUserIsAdmin() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        expectUserExistsCheck(registrationDto.getUsername(), false);
        expectAddUser(registrationDto);
        expectAdminRoleCheck(true);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin?confirmation=created"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    public void registerUserShouldAutoLoginUserAndRedirectToUserViewWhenCurrentUserIsNotAdmin() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("test123")
                .withConfirmPassword("test123")
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        expectUserExistsCheck(registrationDto.getUsername(), false);
        expectAddUser(registrationDto);
        expectAdminRoleCheck(false);
        expectAutoLogin(registrationDto.getUsername(), registrationDto.getPassword());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/register").session(mockHttpSession)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + registrationDto.getUsername() + "/view?confirmation=created"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    private void expectFindAllRoles() {
        EasyMock.expect(roleServiceMock.findAll()).andReturn(roles);
    }

    private void expectFindRoleByTypeThrowsEntityNotFoundException() {
        EasyMock.expect(roleServiceMock.findByType(RoleType.ROLE_USER))
                .andThrow(new EntityNotFoundException());
    }

    private void expectFindRoleByType() {
        EasyMock.expect(roleServiceMock.findByType(RoleType.ROLE_USER)).andReturn(userRole);
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

    private void expectAnonymousAuthCheck(boolean anonymous) {
        EasyMock.expect(securityServiceMock.isCurrentAuthenticationAnonymous()).andReturn(anonymous);
    }

    private void expectGetLoggedInUser() {
        EasyMock.expect(userServiceMock.getLoggedInUser()).andReturn(loggedInUser);
    }

    private void expectAdminRoleCheck(boolean userIsAdmin) {
        EasyMock.expect(securityServiceMock.currentAuthenticationHasRole(RoleType.ROLE_ADMIN))
                .andReturn(userIsAdmin);
    }

    private void expectAutoLogin(String username, String password) {
        securityServiceMock.autoLogin(username, password);
        EasyMock.expectLastCall();
    }

    private static Role newRole(long id, RoleType roleType) {
        return RoleBuilder.givenRole().withId(id).withType(roleType).build();
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
