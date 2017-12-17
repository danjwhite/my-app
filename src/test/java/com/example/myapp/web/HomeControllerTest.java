package com.example.myapp.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

public class HomeControllerTest {

    @Test
    public void testHomePage() throws Exception {
        // Create controller.
        HomeController controller = new HomeController();

        // Set up MockMvc to use controller.
        MockMvc mockMvc = standaloneSetup(controller).build();

        // Perform GET request and assert expectations.
        mockMvc.perform(get("/"))
                .andExpect(view().name("home"));
    }
}
