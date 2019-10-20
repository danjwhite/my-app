package com.example.myapp.web.controller;

import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import com.example.myapp.test.WebMvcBaseTest;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = {HomeControllerTest.HomeControllerTestConfig.class})
public class HomeControllerTest extends WebMvcBaseTest {

    private static final SecurityService securityServiceMock = EasyMock.strictMock(SecurityService.class);
    private static final UserService userServiceMock = EasyMock.strictMock(UserService.class);
    private static final UserDetails userDetailsMock = EasyMock.strictMock(UserDetails.class);

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession mockHttpSession;
    private User loggedInUser;

    @BeforeClass
    public static void init() {
        initMocks(securityServiceMock, userServiceMock, userDetailsMock);
    }

    @Before
    public void setUp() {
        mockHttpSession = new MockHttpSession();

        loggedInUser = UserBuilder.givenUser()
                .withId(1L)
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword(new BCryptPasswordEncoder().encode("test123"))
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void homeShouldReturnExpectedViewWithExpectedAttributes() throws Exception {

        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("home"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    private void expectGetLoggedInUser() {
        EasyMock.expect(userServiceMock.getLoggedInUser()).andReturn(loggedInUser);
    }

    private Role newRole(long id, RoleType roleType) {
        return RoleBuilder.givenRole().withId(id).withType(roleType).build();
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
