package com.example.myapp.web.controller;

import com.example.myapp.domain.RoleType;
import com.example.myapp.test.WebMvcBaseTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

// TODO: Add test to verify the authentication after actual login
@WebMvcTest(LoginController.class)
public class LoginControllerTest extends WebMvcBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void init() {
        initMocks();
    }

    @Test
    public void loginShouldReturnExpectedView() throws Exception {
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("loginForm"))
                .andExpect(MockMvcResultMatchers.model().attribute("userRole", RoleType.ROLE_USER));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void logoutShouldLogUserOutOfSecurityContextWhenAuthIsPresent() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertEquals("mjones", auth.getName());

        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?logout"));

        verifyAll();

        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class LoginControllerTestConfig {

        @Bean
        public LoginController loginController() {
            return new LoginController();
        }
    }
}
