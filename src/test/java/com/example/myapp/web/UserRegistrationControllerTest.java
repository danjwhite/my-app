package com.example.myapp.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    public void testRegisterUserSuccess() throws Exception {

        // Perform POST request to register a user on MockMvc and assert expectations.
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("lastName", "Smith")
                .param("username", "jsmith")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(redirectedUrl("/account/view?userId=5&confirmation=created"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testRegisterUserPasswordNotMatching() throws Exception {

        // Perform POST request to register a user on MockMvc and assert expectations.
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("lastName", "Smith")
                .param("username", "jsmith")
                .param("password", "password123")
                .param("confirmPassword", "password456"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode("user", "confirmPassword", "FieldMatch"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterUserAccountExists() throws Exception {

        // Perform POST request to register a user on MockMvc and assert expectations.
        mockMvc.perform(post("/register")
                .param("firstName", "Michael")
                .param("lastName", "Jones")
                .param("username", "mjones")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("user", "username"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterUserBlankFields() throws Exception {

        // Perform POST request to register a user on MockMvc with empty fields and assert expectations.
        mockMvc.perform(post("/register")
                .param("firstName", "")
                .param("lastName", "")
                .param("username", "")
                .param("password", "")
                .param("confirmPassword", ""))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode("user", "firstName", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("user", "lastName", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("user", "username", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("user", "password", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("user", "confirmPassword", "NotBlank"))
                .andExpect(status().isOk());
    }
}
