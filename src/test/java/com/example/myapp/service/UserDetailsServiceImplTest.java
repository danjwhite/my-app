package com.example.myapp.service;

import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.dao.UserRepository;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import org.easymock.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(EasyMockRunner.class)
public class UserDetailsServiceImplTest extends EasyMockSupport {

    @Mock(type = MockType.STRICT)
    private UserRepository userRepositoryMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserDetailsServiceImpl userDetailsService;

    @Before
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepositoryMock);
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage("Invalid username or password");

        String username = "mjones";

        expectFindByUsername(username, null);
        replayAll();

        userDetailsService.loadUserByUsername(username);
        verifyAll();
    }

    @Test
    public void loadUserByUsernameShouldReturnExpectedResult() {
        String username = "mjones";

        Set<Role> roles = new HashSet<>();
        roles.add(RoleBuilder.givenRole().withType(RoleType.ROLE_USER).build());
        roles.add(RoleBuilder.givenRole().withType(RoleType.ROLE_ADMIN).build());

        User user = UserBuilder.givenUser().withUsername(username)
                .withPassword("test123")
                .withRoles(roles)
                .build();

        expectFindByUsername(username, user);
        replayAll();

        UserDetails result = userDetailsService.loadUserByUsername(username);
        verifyAll();

        Assert.assertNotNull(result);
        Assert.assertEquals(user.getUsername(), result.getUsername());
        Assert.assertEquals(user.getPassword(), result.getPassword());

        Set<String> expectedRoletypes = user.getRoles().stream().map(role -> role.getType().name()).collect(Collectors.toSet());
        Set<String> actualRoletypes = result.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        Assert.assertEquals(expectedRoletypes, actualRoletypes);
    }

    private void expectFindByUsername(String username, User user) {
        EasyMock.expect(userRepositoryMock.findByUsername(username)).andReturn(user);
    }
}
