package com.example.myapp.web.controller;

import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
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

import java.util.Collections;

@WebMvcTest(AccountController.class)
@ContextConfiguration(classes = {AccountControllerTest.AccountControllerTestConfig.class})
public class AccountControllerTest extends WebMvcBaseTest {

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
    public void getAccountPageShouldReturnExpectedResult() throws Exception {
        final User loggedInUser = newUser();

        expectGetLoggedInUser(loggedInUser);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/account")
                .session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser));

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    private void expectGetLoggedInUser(User user) {
        EasyMock.expect(userServiceMock.getLoggedInUser())
                .andReturn(user);
    }

    private User newUser() {
        Role role = RoleBuilder.givenRole().withId(1L)
                .withType(RoleType.ROLE_USER).build();

        return UserBuilder.givenUser()
                .withId(1L)
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword("password")
                .withRoles(Collections.singleton(role))
                .build();
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class AccountControllerTestConfig {

        @Bean
        public AccountController accountController() {
            return new AccountController(userServiceMock);
        }
    }
}
