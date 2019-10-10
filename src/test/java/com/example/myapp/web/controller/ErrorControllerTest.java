package com.example.myapp.web.controller;

import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.User;
import com.example.myapp.service.UserService;
import com.example.myapp.test.WebMvcBaseTest;
import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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

    @BeforeClass
    public static void init() {
        initMocks(userServiceMock);
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getErrorShouldDisplayExpectedAttributesWhenErrorIs403Forbidden() throws Exception {
        User loggedInUser = newUser();

        expectGetLoggedInUser(loggedInUser);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/error/403"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "errorTitle", "errorDescription"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("errorTitle", "403: Forbidden"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorDescription", "The request could not be completed."));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getErrorShouldDisplayExpectedAttributesWhenErrorIs404NotFound() throws Exception {
        User loggedInUser = newUser();

        expectGetLoggedInUser(loggedInUser);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/error/404"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "errorTitle", "errorDescription"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("errorTitle", "404: Resource Not Found"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorDescription", "The request could not be completed."));

        verifyAll();
    }

    private void expectGetLoggedInUser(User user) {
        EasyMock.expect(userServiceMock.getLoggedInUser()).andReturn(user);
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
