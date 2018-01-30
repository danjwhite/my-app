package com.example.myapp.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.junit.Assert.*;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private IUserService userService;

    private MockMvc mockMvc;

    @Before
    public void setup() {

        // Setup MockMvc to use UserRegistrationController.
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void shouldShowLoginForm() throws Exception {

        // Perform GET request and assert expectations.
        mockMvc.perform(get("/login"))
                .andExpect(view().name("loginForm"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testGetUserAccount() throws Exception {

        // Create expected object.
        User expectedUser = userService.findById(1L);

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/account/view?userId=1"))
                .andExpect(view().name("user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", expectedUser));
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void shouldShowAccountForm() throws Exception {

        // Get user and user properties.
        User user = userService.findById(1L);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/account/edit/info?userId=1"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("id", equalTo(1L))))
                .andExpect(model().attribute("user", hasProperty("firstName", is(firstName))))
                .andExpect(model().attribute("user", hasProperty("lastName", is(lastName))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void updateUserInfo() throws Exception {

        // Get user and user properties.
        User user = userService.findById(1L);
        String lastName = user.getLastName();

        // Perform POST request on MockMvc to update the user's first name and assert expectations.
        mockMvc.perform(post("/account/edit/info?userId=1")
                .param("id", "1")
                .param("firstName", "Mike")
                .param("lastName", lastName))
                .andExpect(redirectedUrl("/account/view?userId=1&confirmation=infoUpdated"));
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void shouldShowPasswordForm() throws Exception {

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/account/edit/password?userId=1"))
                .andExpect(model().attributeExists("userPasswordDto"))
                .andExpect(model().attribute("userPasswordDto", hasProperty("userId", equalTo(1L))))
                .andExpect(model().attribute("userPasswordDto", hasProperty("password", nullValue())))
                .andExpect(model().attribute("userPasswordDto", hasProperty("newPassword", nullValue())))
                .andExpect(model().attribute("userPasswordDto", hasProperty("confirmNewPassword", nullValue())));
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testUpdatePassword() throws Exception {

        // Perform POST request on MockMvc and assert expectations.
        mockMvc.perform(post("/account/edit/password?userId=1")
                .param("userId", "1")
                .param("password", "password123")
                .param("newPassword", "password456")
                .param("confirmNewPassword", "password456"))
                .andExpect(redirectedUrl("/account/view?userId=1&confirmation=passwordUpdated"));
    }
}
