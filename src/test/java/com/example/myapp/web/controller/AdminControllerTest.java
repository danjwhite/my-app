package com.example.myapp.web.controller;

import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.User;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = {AdminControllerTest.AdminControllerTestConfig.class})
public class AdminControllerTest extends WebMvcBaseTest {

    @Autowired
    private MockMvc mockMvc;

    private static UserService userServiceMock;

    @BeforeClass
    public static void init() {
        userServiceMock = EasyMock.strictMock(UserService.class);
        initMocks(userServiceMock);
    }

    @Before
    public void setUp() {
        resetAll();
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
        User loggedInUser = newUser(1L);
        List<User> users = LongStream.range(2, 6).mapToObj(this::newUser).collect(Collectors.toList());

        expectGetLoggedInUser(loggedInUser);
        expectFindAllUsers(users);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "users"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("users", users));

        verifyAll();
    }

    private void expectGetLoggedInUser(User user) {
        EasyMock.expect(userServiceMock.getLoggedInUser()).andReturn(user);
    }

    private void expectFindAllUsers(List<User> users) {
        EasyMock.expect(userServiceMock.findAll()).andReturn(users);
    }

    private User newUser(long id) {
        return UserBuilder.givenUser().withId(id).build();
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