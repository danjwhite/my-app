package com.example.myapp.web.controller;

import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.service.UserInContextService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = {AdminControllerTest.AdminControllerTestConfig.class})
public class AdminControllerTest extends WebMvcBaseTest {

    private static final UserInContextService userInContextServiceMock = EasyMock.strictMock(UserInContextService.class);

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession mockHttpSession;

    @BeforeClass
    public static void init() {
        initMocks(userInContextServiceMock);
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
    public void getAdminPageShouldReturnExpectedResultWhenUserIsAdmin() throws Exception {
        final User loggedInUser = newUser();
        final List<RoleType> roles = Arrays.stream(RoleType.values()).collect(Collectors.toList());

        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                .session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("roles", roles))
                .andExpect(MockMvcResultMatchers.model().attribute("defaultRole", RoleType.ROLE_USER));

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));

        verifyAll();
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
    static class AdminControllerTestConfig {

        @Bean
        public AdminController adminController() {
            return new AdminController(userInContextServiceMock);
        }
    }
}
