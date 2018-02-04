package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.service.IUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UserControllerTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {

        // Setup MockMvc to use WebApplicationContext.
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    public void shouldShowLoginForm() throws Exception {

        // Perform GET request and assert expectations.
        mockMvc.perform(get("/login"))
                .andExpect(view().name("loginForm"))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginSuccess() throws Exception {

        // Perform login on MockMvc and assert expectations.
        mockMvc.perform(formLogin().user("mjones").password("password123"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("mjones"));
    }

    @Test
    public void testLoginDenied() throws Exception {

        // Perform login on MockMvc and assert expectations.
        mockMvc.perform(formLogin().user("invalid").password("invalid"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testGetUserAccountSuccess() throws Exception {

        // Create expected object.
        User expectedUser = userService.findByUsername("mjones");

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/user/mjones/view"))
                .andExpect(view().name("user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", expectedUser));
    }

    @Test
    @WithMockUser(username = "drodman", password = "password123", roles = {"USER"})
    public void testGetUserAccountForbidden() throws Exception {

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/user/mjones/view"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void shouldShowAccountForm() throws Exception {

        // Get user and user properties.
        User user = userService.findByUsername("mjones");
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/user/mjones/edit/info"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("username", is("mjones"))))
                .andExpect(model().attribute("user", hasProperty("firstName", is(firstName))))
                .andExpect(model().attribute("user", hasProperty("lastName", is(lastName))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void updateUserInfo() throws Exception {

        // Get user and user properties.
        User user = userService.findByUsername("mjones");
        String lastName = user.getLastName();

        // Perform POST request on MockMvc to update the user's first name and assert expectations.
        mockMvc.perform(post("/user/mjones/edit/info")
                .with(csrf())
                .param("username", "mjones")
                .param("firstName", "Mike")
                .param("lastName", lastName))
                .andExpect(redirectedUrl("/user/mjones/view?confirmation=infoUpdated"));
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void shouldShowPasswordForm() throws Exception {

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/user/mjones/edit/password"))
                .andExpect(model().attributeExists("userPasswordDto"))
                .andExpect(model().attribute("userPasswordDto", hasProperty("username", is("mjones"))))
                .andExpect(model().attribute("userPasswordDto", hasProperty("password", nullValue())))
                .andExpect(model().attribute("userPasswordDto", hasProperty("newPassword", nullValue())))
                .andExpect(model().attribute("userPasswordDto", hasProperty("confirmNewPassword", nullValue())));
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testUpdatePassword() throws Exception {

        // Perform POST request on MockMvc and assert expectations.
        mockMvc.perform(post("/user/mjones/edit/password")
                .with(csrf())
                .param("username", "mjones")
                .param("password", "password123")
                .param("newPassword", "password456")
                .param("confirmNewPassword", "password456"))
                .andExpect(redirectedUrl("/user/mjones/view?confirmation=passwordUpdated"));
    }
}
