package com.example.myapp.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UserRegistrationControllerTest {

    @Autowired
    private UserRegistrationController userRegistrationController;

    private MockMvc mockMvc;

    @Before
    public void setup() {

        // Setup MockMvc to use UserRegistrationController.
        this.mockMvc = MockMvcBuilders.standaloneSetup(userRegistrationController).build();
    }

    @Test
    public void shouldShowUserRegistrationForm() throws Exception {

        // Perform GET request on MockMvc to register a user and assert expectations.
        mockMvc.perform(get("/register"))
                .andExpect(view().name("registrationForm"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("firstName", is(nullValue()))))
                .andExpect(model().attribute("user", hasProperty("lastName", is(nullValue()))))
                .andExpect(model().attribute("user", hasProperty("username", is(nullValue()))))
                .andExpect(model().attribute("user", hasProperty("password", is(nullValue()))))
                .andExpect(model().attribute("user", hasProperty("confirmPassword", is(nullValue()))));
    }
}
