package com.example.myapp.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.example.myapp.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class HomeControllerTest {

    @Autowired
    private HomeController controller;

    @Test
    public void testHomePage() throws Exception {

        // Set up MockMvc to use controller.
        MockMvc mockMvc = standaloneSetup(controller).build();

        // Perform GET request and assert expectations.
        mockMvc.perform(get("/"))
                .andExpect(view().name("home"));
    }
}
