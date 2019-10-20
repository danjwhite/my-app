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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = {AdminControllerTest.AdminControllerTestConfig.class})
public class AdminControllerTest extends WebMvcBaseTest {

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
    public void getAdminPageShouldRedirectToErrorPageWhenUserIsNotAdmin() throws Exception {
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones", roles = {"USER", "ADMIN"})
    public void getAdminPageShouldShowAdminPageWhenUserIsAdmin() throws Exception {
        User loggedInUser = newUser();
        List<User> users = LongStream.range(2, 6).mapToObj(id -> newUser()).collect(Collectors.toList());

        expectGetLoggedInUser(loggedInUser);
        expectFindAllUsers(users);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/admin").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "users"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("users", users));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    private void expectGetLoggedInUser(User user) {
        EasyMock.expect(userServiceMock.getLoggedInUser()).andReturn(user);
    }

    private void expectFindAllUsers(List<User> users) {
        EasyMock.expect(userServiceMock.findAll()).andReturn(users);
    }

    private User newUser() {
        return UserBuilder.givenUser().withId(1L).build();
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class AdminControllerTestConfig {

        @Bean
        public AdminController adminController() {
            return new AdminController(userServiceMock);
        }
    }
}
