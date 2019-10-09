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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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

    @BeforeClass
    public static void init() {
        initMocks(userServiceMock, roleServiceMock, securityServiceMock);
    }

    @Test
    public void showRegistrationFormShouldRedirectTo404NotFoundErrorPageWhenFindRoleByTypeThrowsEntityNotFoundException() throws Exception {
        expectFindAllRoles();
        expectFindRoleByTypeThrowsEntityNotFoundException();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    public void showRegistrationFormShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final UserRegistrationDto registrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withRoles(Collections.singleton(userRole))
                .build();

        expectFindAllRoles();
        expectFindRoleByType();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allRoles", "user"))
                .andExpect(MockMvcResultMatchers.model().attribute("allRoles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("user", registrationDto));

        verifyAll();
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin?confirmation=created"));


        verifyAll();
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

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("user", registrationDto))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/" + registrationDto.getUsername() + "/view?confirmation=created"));


        verifyAll();
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
