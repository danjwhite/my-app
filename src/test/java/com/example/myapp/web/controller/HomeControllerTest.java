package com.example.myapp.web.controller;

import com.example.myapp.web.controller.HomeController;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
@Ignore
public class HomeControllerTest {

    @Autowired
    private HomeController homeController;

    @Test
    public void testHomePage() throws Exception {

        // Set up MockMvc to use HomeController.
        MockMvc mockMvc = standaloneSetup(homeController).build();

        // Perform GET request and assert expectations.
        mockMvc.perform(get("/"))
                .andExpect(view().name("home"));
    }
}
