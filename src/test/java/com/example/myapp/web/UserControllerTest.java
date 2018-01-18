package com.example.myapp.web;

import org.junit.Before;
import org.junit.Ignore;
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
@Ignore
public class UserControllerTest {

    @Autowired
    private UserController userController;

    private MockMvc mockMvc;

    @Before
    public void setup() {

        // Setup MockMvc to use UserRegistrationController.
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }
}
