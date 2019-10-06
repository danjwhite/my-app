package com.example.myapp.web.controller;

import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.User;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import com.example.myapp.test.WebMvcBaseTest;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = {HomeControllerTest.HomeControllerTestConfig.class})
public class HomeControllerTest extends WebMvcBaseTest {

    @Autowired
    private MockMvc mockMvc;

    private static SecurityService securityServiceMock;
    private static UserService userServiceMock;
    private static UserDetails userDetailsMock;

    @BeforeClass
    public static void init() {
        securityServiceMock = EasyMock.strictMock(SecurityService.class);
        userServiceMock = EasyMock.strictMock(UserService.class);
        userDetailsMock = EasyMock.strictMock(UserDetails.class);
        initMocks(securityServiceMock, userServiceMock, userDetailsMock);
    }

    @Before
    public void setUp() {
        resetAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void homeShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final String username = "mjones";
        User loggedInUser = newUser(username);

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, loggedInUser);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("home"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", loggedInUser));

        verifyAll();
    }

    private void expectGetAuthenticationPrincipal(String username) {
        EasyMock.expect(securityServiceMock.getPrincipal())
                .andReturn(userDetailsMock);
        EasyMock.expect(userDetailsMock.getUsername())
                .andReturn(username);
    }

    private void expectFindUserByUsername(String username, User user) {
        EasyMock.expect(userServiceMock.findByUsername(username))
                .andReturn(user);
    }

    private User newUser(String username) {
        return UserBuilder.givenUser().withUsername(username).build();
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class HomeControllerTestConfig {

        @Bean
        public HomeController homeController() {
            return new HomeController(securityServiceMock, userServiceMock);
        }
    }
}
