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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
public class ErrorControllerTest {

    @Autowired
    private ErrorController errorController;

    @Autowired
    private IUserService userService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        // Setup MockMvc to use ErrorController.
        this.mockMvc = MockMvcBuilders.standaloneSetup(errorController).build();
    }

    @Test
    public void testGetError403() throws Exception {

        // Get expected user.
        User expectedUser = userService.getLoggedInUser();

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/error/403"))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", expectedUser))
                .andExpect(model().attributeExists("errorTitle"))
                .andExpect(model().attribute("errorTitle", "403: Forbidden"))
                .andExpect(model().attributeExists("errorDescription"))
                .andExpect(model().attribute("errorDescription", "The request could not be completed."))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetError404() throws Exception {
        // Get expected user.
        User expectedUser = userService.getLoggedInUser();

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/error/404"))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", expectedUser))
                .andExpect(model().attributeExists("errorTitle"))
                .andExpect(model().attribute("errorTitle", "404: Resource Not Found"))
                .andExpect(model().attributeExists("errorDescription"))
                .andExpect(model().attribute("errorDescription", "The request could not be completed."))
                .andExpect(status().isOk());
    }
}
