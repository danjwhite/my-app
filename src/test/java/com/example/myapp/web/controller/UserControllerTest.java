package com.example.myapp.web.controller;

import com.example.myapp.builder.dto.UserDTOBuilder;
import com.example.myapp.builder.dto.UserPasswordDtoBuilder;
import com.example.myapp.builder.dto.UserRegistrationDtoBuilder;
import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.UserService;
import com.example.myapp.test.TestUtil;
import com.example.myapp.test.WebMvcBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.collection.IsMapWithSize;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserControllerTest.UserControllerTestConfig.class})
public class UserControllerTest extends WebMvcBaseTest {

    private static final UserService userServiceMock = EasyMock.strictMock(UserService.class);

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void init() {
        initMocks(userServiceMock);
    }

    @Test                                                                                                                                                       
    @WithMockUser(username = "mjones")
    public void getUserShouldReturnExpectedResult() throws Exception {
        final User user = newUser();
        final UserDTO userDTO = new UserDTO(user);

        expectFindByUserName(user.getUsername(), user);
        replayAll();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user.getUsername())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        verifyAll();

        Assert.assertEquals(userDTO, TestUtil.jsonToObject(result.getResponse().getContentAsString(), UserDTO.class));
    }

    // ------------------------------------------ ADD USER - FIRST NAME ------------------------------------------

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenFirstNameIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setFirstName(null);

        addUserAndExpectFieldError(dto, "firstName", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenFirstNameIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setFirstName(StringUtils.EMPTY);

        addUserAndExpectFieldError(dto, "firstName", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenFirstNameIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setFirstName(StringUtils.SPACE);

        addUserAndExpectFieldError(dto, "firstName", "Cannot be blank");
    }

    // ------------------------------------------ ADD USER - LAST NAME ------------------------------------------

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenLastNameIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setLastName(null);

        addUserAndExpectFieldError(dto, "lastName", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenLastNameIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setLastName(StringUtils.EMPTY);

        addUserAndExpectFieldError(dto, "lastName", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenLastNameIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setLastName(StringUtils.SPACE);

        addUserAndExpectFieldError(dto, "lastName", "Cannot be blank");
    }

    // ------------------------------------------ ADD USER - USERNAME ------------------------------------------

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenUsernameIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setUsername(null);

        addUserAndExpectFieldError(dto, "username", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenUsernameIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setUsername(StringUtils.EMPTY);

        addUserAndExpectFieldError(dto, "username", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenUsernameIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setUsername(StringUtils.SPACE);

        addUserAndExpectFieldError(dto, "username", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenUsernameAlreadyExists() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();

        expectUserExistsCheck(dto.getUsername(), true);
        addUserAndExpectFieldError(dto, "username", "There is already an account registered with this username.");
    }

    // ------------------------------------------ ADD USER - PASSWORD ------------------------------------------

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenPasswordIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setPassword(null);

        addUserAndExpectFieldError(dto, "password", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenPasswordIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setPassword(StringUtils.EMPTY);

        addUserAndExpectFieldError(dto, "password", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenPasswordIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setPassword(StringUtils.SPACE);

        addUserAndExpectFieldError(dto, "password", "Cannot be blank");
    }

    // ------------------------------------------ ADD USER - CONFIRM PASSWORD ------------------------------------------

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenConfirmPasswordIsNull() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setConfirmPassword(null);

        addUserAndExpectFieldError(dto, "confirmPassword", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenConfirmPasswordIsEmpty() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setConfirmPassword(StringUtils.EMPTY);

        addUserAndExpectFieldError(dto, "confirmPassword", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenConfirmPasswordIsBlank() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setConfirmPassword(StringUtils.SPACE);

        addUserAndExpectFieldError(dto, "confirmPassword", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenPasswordFieldsDoNotMatch() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setConfirmPassword("test");

        addUserAndExpectFieldError(dto, "confirmPassword", "Password fields must match.");
    }

    // ------------------------------------------ ADD USER - ROLES ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultWhenNoRolesArePresent() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();
        dto.setRoleTypes(Collections.emptyList());

        addUserAndExpectFieldError(dto, "roleTypes", "At least one role must be selected.");
    }

    // ------------------------------------------ ADD USER - SUCCESS ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void addUserShouldReturnExpectedResultOnSuccess() throws Exception {
        UserRegistrationDto dto = newUserRegistrationDto();

        expectUserExistsCheck(dto.getUsername(), false);
        expectAddUser(dto);

        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(dto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(TestUtil.emptyResponse());

        verifyAll();
    }

    // ------------------------------------------ UPDATE USER - FIRST NAME ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void updateUserShouldReturnExpectedResultWhenFirstNameIsNull() throws Exception {
        UserDTO dto = newUserDto();
        dto.setFirstName(null);

        updateUserAndExpectFieldError(dto, "firstName", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserShouldReturnExpectedResultWhenFirstNameIsEmpty() throws Exception {
        UserDTO dto = newUserDto();
        dto.setFirstName(StringUtils.EMPTY);

        updateUserAndExpectFieldError(dto, "firstName", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserShouldReturnExpectedResultWhenFirstNameIsBlank() throws Exception {
        UserDTO dto = newUserDto();
        dto.setFirstName(StringUtils.SPACE);

        updateUserAndExpectFieldError(dto, "firstName", "Cannot be blank");
    }

    // ------------------------------------------ UPDATE USER - LAST NAME ------------------------------------------

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserShouldReturnExpectedResultWhenLastNameIsNull() throws Exception {
        UserDTO dto = newUserDto();
        dto.setLastName(null);

        updateUserAndExpectFieldError(dto, "lastName", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserShouldReturnExpectedResultWhenLastNameIsEmpty() throws Exception {
        UserDTO dto = newUserDto();
        dto.setLastName(StringUtils.EMPTY);

        updateUserAndExpectFieldError(dto, "lastName", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateUserShouldReturnExpectedResultWhenLastNameIsBlank() throws Exception {
        UserDTO dto = newUserDto();
        dto.setLastName(StringUtils.SPACE);

        updateUserAndExpectFieldError(dto, "lastName", "Cannot be blank");
    }

    // ------------------------------------------ UPDATE USER - ROLES ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void updateUserShouldReturnExpectedResultWhenNoRolesArePresent() throws Exception {
        UserDTO dto = newUserDto();
        dto.setRoleTypes(Collections.emptyList());

        updateUserAndExpectFieldError(dto, "roleTypes", "At least one role must be selected.");
    }

    // ------------------------------------------ UPDATE USER - SUCCESS ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void updateUserShouldReturnExpectedResultOnSuccess() throws Exception {
        UserDTO dto = newUserDto();

        expectUpdateUser(dto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(dto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(TestUtil.emptyResponse());

        verifyAll();
    }

    // ------------------------------------------ UPDATE PASSWORD - PASSWORD ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenPasswordIsNull() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setPassword(null);

        updatePasswordAndExpectFieldError(dto, "password", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenPasswordIsEmpty() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setPassword(StringUtils.EMPTY);

        updatePasswordAndExpectFieldError(dto, "password", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenPasswordIsBlank() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setPassword(StringUtils.SPACE);

        updatePasswordAndExpectFieldError(dto, "password", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenPasswordIsInvalid() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setPassword("test");

        User user = newUser();
        Assert.assertFalse(BCrypt.checkpw(dto.getPassword(), user.getPassword()));

        expectFindByUserName(dto.getUsername(), newUser());
        updatePasswordAndExpectFieldError(dto, "password", "Current password is invalid.");
    }

    // ------------------------------------------ UPDATE PASSWORD - NEW PASSWORD ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenNewPasswordIsNull() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setNewPassword(null);

        updatePasswordAndExpectFieldError(dto, "newPassword", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenNewPasswordIsEmpty() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setNewPassword(StringUtils.EMPTY);

        updatePasswordAndExpectFieldError(dto, "newPassword", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenNewPasswordIsBlank() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setNewPassword(StringUtils.SPACE);

        updatePasswordAndExpectFieldError(dto, "newPassword", "Cannot be blank");
    }

    // ------------------------------------------ UPDATE PASSWORD - CONFIRM PASSWORD ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenConfirmPasswordIsNull() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setConfirmPassword(null);

        updatePasswordAndExpectFieldError(dto, "confirmPassword", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenConfirmPasswordIsEmpty() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setConfirmPassword(StringUtils.EMPTY);

        updatePasswordAndExpectFieldError(dto, "confirmPassword", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenConfirmPasswordIsBlank() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setConfirmPassword(StringUtils.SPACE);

        updatePasswordAndExpectFieldError(dto, "confirmPassword", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultWhenPasswordFieldsDoNotMatch() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        dto.setConfirmPassword("test");

        Assert.assertNotEquals(dto.getNewPassword(), dto.getConfirmPassword());

        updatePasswordAndExpectFieldError(dto, "confirmPassword", "The password fields must match.");
    }

    // ------------------------------------------ UPDATE PASSWORD - SUCCESS ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void updatePasswordShouldReturnExpectedResultOnSuccess() throws Exception {
        UserPasswordDto dto = newPasswordDto();
        User user = newUser();

        Assert.assertTrue(BCrypt.checkpw(dto.getPassword(), user.getPassword()));

        expectFindByUserName(dto.getUsername(), user);
        expectUpdatePassword(dto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/password")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(dto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(TestUtil.emptyResponse());

        verifyAll();
    }

    // ------------------------------------------ DELETE USER ------------------------------------------
    @Test
    @WithMockUser(username = "mjones")
    public void deleteUserShouldReturnExpectedResult() throws Exception {
        String username = "mjones";

        expectDeleteUser(username);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + username)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(TestUtil.emptyResponse());

        verifyAll();
    }

    public void addUserAndExpectFieldError(UserRegistrationDto dto, String fieldName, String message) throws Exception {
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapWithSize.aMapWithSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapContaining.hasEntry(fieldName, message)));

        verifyAll();
    }

    public void updateUserAndExpectFieldError(UserDTO dto, String fieldName, String message) throws Exception {
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapWithSize.aMapWithSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapContaining.hasEntry(fieldName, message)));

        verifyAll();
    }

    public void updatePasswordAndExpectFieldError(UserPasswordDto dto, String fieldName, String message) throws Exception {
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/password")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
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

    private void expectFindByUserName(String username, User user) {
        EasyMock.expect(userServiceMock.findByUsername(username)).andReturn(user);
    }

    private void expectUpdateUser(UserDTO user) {
        EasyMock.expect(userServiceMock.update(user))
                .andReturn(new User());
    }

    private void expectUpdatePassword(UserPasswordDto userPasswordDto) {
        EasyMock.expect(userServiceMock.updatePassword(userPasswordDto)).andReturn(new User());
    }

    private void expectDeleteUser(String username) {
        userServiceMock.delete(username);
        EasyMock.expectLastCall();
    }

    private Role newRole() {
        return RoleBuilder.givenRole().withId(1L).withType(RoleType.ROLE_USER).build();
    }

    private User newUser() {
        return UserBuilder.givenUser()
                .withId(1L)
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword(new BCryptPasswordEncoder().encode("password"))
                .withRoles(Collections.singleton(newRole()))
                .build();
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

    private UserDTO newUserDto() {
        return UserDTOBuilder.givenUserDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withRoleTypes(Collections.singletonList(RoleType.ROLE_USER))
                .build();
    }

    private UserPasswordDto newPasswordDto() {
        return UserPasswordDtoBuilder.givenUserPasswordDto()
                .withPassword("password")
                .withNewPassword("newpassword")
                .withConfirmPassword("newpassword")
                .build();
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class UserControllerTestConfig {

        @Bean
        public UserController userController() {
            return new UserController(userServiceMock);
        }
    }
}
