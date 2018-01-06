package com.example.myapp.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class HomeControllerTest {

    @Mock
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
