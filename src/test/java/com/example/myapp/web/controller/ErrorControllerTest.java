package com.example.myapp.web.controller;

import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.User;
import com.example.myapp.service.UserService;
import com.example.myapp.test.WebMvcBaseTest;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ErrorController.class)
@ContextConfiguration(classes = {ErrorControllerTest.ErrorControllerTestConfig.class})
public class ErrorControllerTest extends WebMvcBaseTest {

    private static final UserService userServiceMock = EasyMock.strictMock(UserService.class);

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession mockHttpSession;

    @BeforeClass
    public static void init() {
        initMocks(userServiceMock);
    }

    @Before
    public void setUp() {
        mockHttpSession = new MockHttpSession();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getErrorShouldDisplayExpectedAttributesWhenErrorIs403Forbidden() throws Exception {
        User loggedInUser = newUser();

        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/error/403").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "errorTitle", "errorDescription"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("errorTitle", "403: Forbidden"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorDescription", "The request could not be completed."));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getErrorShouldDisplayExpectedAttributesWhenErrorIs404NotFound() throws Exception {
        User loggedInUser = newUser();

        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/error/404").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "errorTitle", "errorDescription"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("errorTitle", "404: Resource Not Found"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorDescription", "The request could not be completed."));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    private User newUser() {
        return UserBuilder.givenUser().withId(1L).build();
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class ErrorControllerTestConfig {

        @Bean
        public ErrorController errorController() {
            return new ErrorController(userServiceMock);
        }
    }
}
