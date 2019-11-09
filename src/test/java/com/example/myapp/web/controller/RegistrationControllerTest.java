package com.example.myapp.web.controller;

import com.example.myapp.builder.dto.UserRegistrationDtoBuilder;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import com.example.myapp.test.TestUtil;
import com.example.myapp.test.WebMvcBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.collection.IsMapWithSize;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

@WebMvcTest(RegistrationController.class)
@ContextConfiguration(classes = {RegistrationControllerTest.RegistrationControllerTestConfig.class})
public class RegistrationControllerTest extends WebMvcBaseTest {

    private static final UserService userServiceMock = EasyMock.strictMock(UserService.class);
    private static final SecurityService securityServiceMock = EasyMock.strictMock(SecurityService.class);

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void init() {
        initMocks(userServiceMock, securityServiceMock);
    }

    // ------------------------------------------ ADD USER - FIRST NAME ------------------------------------------

    @Test
    public void registerUserShouldReturnExpectedResultWhenFirstNameIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setFirstName(null);

        registerUserAndExpectFieldError(dto, "firstName", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenFirstNameIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setFirstName(StringUtils.EMPTY);

        registerUserAndExpectFieldError(dto, "firstName", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenFirstNameIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setFirstName(StringUtils.SPACE);

        registerUserAndExpectFieldError(dto, "firstName", "Cannot be blank");
    }

    // ------------------------------------------ ADD USER - LAST NAME ------------------------------------------

    @Test
    public void registerUserShouldReturnExpectedResultWhenLastNameIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setLastName(null);

        registerUserAndExpectFieldError(dto, "lastName", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenLastNameIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setLastName(StringUtils.EMPTY);

        registerUserAndExpectFieldError(dto, "lastName", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenLastNameIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setLastName(StringUtils.SPACE);

        registerUserAndExpectFieldError(dto, "lastName", "Cannot be blank");
    }

    // ------------------------------------------ ADD USER - USERNAME ------------------------------------------

    @Test
    public void registerUserShouldReturnExpectedResultWhenUsernameIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setUsername(null);

        registerUserAndExpectFieldError(dto, "username", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenUsernameIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setUsername(StringUtils.EMPTY);

        registerUserAndExpectFieldError(dto, "username", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenUsernameIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setUsername(StringUtils.SPACE);

        registerUserAndExpectFieldError(dto, "username", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenUsernameAlreadyExists() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();

        expectUserExistsCheck(dto.getUsername(), true);
        registerUserAndExpectFieldError(dto, "username", "There is already an account registered with this username.");
    }

    // ------------------------------------------ ADD USER - PASSWORD ------------------------------------------

    @Test
    public void registerUserShouldReturnExpectedResultWhenPasswordIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setPassword(null);

        registerUserAndExpectFieldError(dto, "password", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenPasswordIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setPassword(StringUtils.EMPTY);

        registerUserAndExpectFieldError(dto, "password", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenPasswordIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setPassword(StringUtils.SPACE);

        registerUserAndExpectFieldError(dto, "password", "Cannot be blank");
    }

    // ------------------------------------------ ADD USER - CONFIRM PASSWORD ------------------------------------------

    @Test
    public void registerUserShouldReturnExpectedResultWhenConfirmPasswordIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setConfirmPassword(null);

        registerUserAndExpectFieldError(dto, "confirmPassword", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenConfirmPasswordIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setConfirmPassword(StringUtils.EMPTY);

        registerUserAndExpectFieldError(dto, "confirmPassword", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenConfirmPasswordIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setConfirmPassword(StringUtils.SPACE);

        registerUserAndExpectFieldError(dto, "confirmPassword", "Cannot be blank");
    }

    @Test
    public void registerUserShouldReturnExpectedResultWhenPasswordFieldsDoNotMatch() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setConfirmPassword("test");

        registerUserAndExpectFieldError(dto, "confirmPassword", "Password fields must match.");
    }

    // ------------------------------------------ ADD USER - ROLES ------------------------------------------
    @Test
    public void registerUserShouldReturnExpectedResultWhenNoRolesArePresent() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setRoleTypes(Collections.emptyList());

        registerUserAndExpectFieldError(dto, "roleTypes", "At least one role must be selected.");
    }

    // ------------------------------------------ SUCCESS ------------------------------------------

    @Test
    public void registerUserShouldReturnExpectedResultOnSuccess() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();

        expectUserExistsCheck(dto.getUsername(), false);
        expectAddUser(dto);
        expectAutoLogin(dto.getUsername(), dto.getPassword());

        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasErrors").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapWithSize.anEmptyMap()));

        verifyAll();
    }

    public void registerUserAndExpectFieldError(UserRegistrationDto dto, String fieldName, String message) throws Exception {
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasErrors").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapWithSize.aMapWithSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapContaining.hasEntry(fieldName, message)));

        verifyAll();
    }

    private void expectUserExistsCheck(String username, boolean userExists) {
        EasyMock.expect(userServiceMock.userExists(username))
                .andReturn(userExists);
    }

    private void expectAddUser(UserRegistrationDto userRegistrationDto) {
        EasyMock.expect(userServiceMock.add(userRegistrationDto))
                .andReturn(new User());
    }

    private void expectAutoLogin(String username, String password) {
        securityServiceMock.autoLogin(username, password);
        EasyMock.expectLastCall();
    }

    private UserRegistrationDto newUserRegistrationDto() {
        return UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("password")
                .withConfirmPassword("password")
                .withRoleTypes(Collections.singletonList(RoleType.ROLE_USER))
                .build();
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class RegistrationControllerTestConfig {

        @Bean
        public RegistrationController registrationController() {
            return new RegistrationController(userServiceMock, securityServiceMock);
        }
    }
}
