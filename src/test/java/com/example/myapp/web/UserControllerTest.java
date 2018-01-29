package com.example.myapp.web;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
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

    @Autowired
    private ObjectMapper objectMapper;

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

        // Perform GET request on MockMvc to update the user's first name and assert expectations.
        mockMvc.perform(post("/account/edit/info?userId=1")
                .param("id", "1")
                .param("firstName", "Mike")
                .param("lastName", lastName))
                .andExpect(redirectedUrl("/account/view?userId=1&confirmation=infoUpdated"));
    }
}
