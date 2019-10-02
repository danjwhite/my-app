package com.example.myapp.service;

import com.example.myapp.domain.RoleType;
import org.easymock.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(EasyMockRunner.class)
@PowerMockIgnore(value = "javax.security.auth.*")
@PrepareForTest(value = {SecurityService.class, SecurityContextHolder.class})
public class SecurityServiceTest {

    @Mock(type = MockType.STRICT)
    private AuthenticationManager authenticationManagerMock;

    @Mock(type = MockType.STRICT)
    private AuthenticationTrustResolver authenticationTrustResolverMock;

    @Mock(type = MockType.STRICT)
    private UserDetailsService userDetailsServiceMock;

    @Mock(type = MockType.STRICT)
    private SecurityContext securityContextMock;

    @Mock(type = MockType.STRICT)
    private Authentication authenticationMock;

    @Mock(type = MockType.STRICT)
    private UserDetails userDetailsMock;

    @Mock(type = MockType.STRICT)
    private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationTokenMock;

    private SecurityService securityService;

    @Before
    public void setUp() {
        PowerMock.mockStatic(SecurityContextHolder.class);

        securityService = new SecurityService(authenticationManagerMock, authenticationTrustResolverMock, userDetailsServiceMock);
    }

    @Test
    public void getPrincipalShouldReturnExpectedResultWhenAuthenticationIsNull() {
        expectGetAuthenticationFromSecurityContext(null);
        replayAll();

        UserDetails result = securityService.getPrincipal();
        verifyAll();

        Assert.assertNull(result);
    }

    @Test
    public void getPrincipalShouldReturnExpectedResultWhenPrincipalIsNotInstanceOfUserDetails() {
        expectGetAuthenticationFromSecurityContext(authenticationMock);
        expectGetPrincipalFromAuthentication(new Object());
        replayAll();

        UserDetails result = securityService.getPrincipal();
        verifyAll();

        Assert.assertNull(result);
    }

    @Test
    public void getPrincipalShouldReturnExpectedResultWhenPrincipalIsInstanceOfUserDetails() {
        expectGetAuthenticationFromSecurityContext(authenticationMock);
        expectGetPrincipalFromAuthentication(userDetailsMock);
        replayAll();

        UserDetails result = securityService.getPrincipal();
        verifyAll();

        Assert.assertEquals(userDetailsMock, result);
    }

    @Test
    public void isCurrentAuthenticationAnonymousShouldReturnExpectedResult() {
        expectGetAuthenticationFromSecurityContext(authenticationMock);
        expectIsAnonymousCheck(false);
        replayAll();

        boolean result = securityService.isCurrentAuthenticationAnonymous();
        verifyAll();

        Assert.assertFalse(result);
    }

    @Test
    public void currentAuthenticationHasRoleShouldReturnExpectedResultWhenFalse() {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        expectGetAuthenticationFromSecurityContext(authenticationMock);
        expectGetAuthoritiesFromAuthentication(authorities);
        replayAll();

        boolean result = securityService.currentAuthenticationHasRole(RoleType.ROLE_ADMIN);
        verifyAll();

        Assert.assertFalse(result);
    }

    @Test
    public void currentAuthenticationHasRoleShouldReturnExpectedResultWhenTrue() {
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN"));

        expectGetAuthenticationFromSecurityContext(authenticationMock);
        expectGetAuthoritiesFromAuthentication(authorities);
        replayAll();

        boolean result = securityService.currentAuthenticationHasRole(RoleType.ROLE_ADMIN);
        verifyAll();

        Assert.assertTrue(result);
    }

    @Test
    public void autoLoginShouldNotSetAuthenticationWhenUsernamePasswordAuthenticationTokenNotAuthenticated() throws Exception {
        String username = "mjones";
        String password = "test123";
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        expectLoadUserByUsername(username);
        expectGetAuthoritiesFromUserDetails(authorities);
        expectNewUsernamePasswordAuthenticationToken(password, authorities);
        expectAuthenticateWithAuthenticationManager();
        expectIsAuthenticatedCheckOnUsernamePasswordAuthenticationToken(false);
        replayAll();

        securityService.autoLogin(username, password);
        verifyAll();
    }

    @Test
    public void autoLoginShouldSetAuthenticationWhenUsernamePasswordAuthenticationTokenIsAuthenticated() throws Exception {
        String username = "mjones";
        String password = "test123";
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        expectLoadUserByUsername(username);
        expectGetAuthoritiesFromUserDetails(authorities);
        expectNewUsernamePasswordAuthenticationToken(password, authorities);
        expectAuthenticateWithAuthenticationManager();
        expectIsAuthenticatedCheckOnUsernamePasswordAuthenticationToken(true);
        expectSetAuthentication();
        replayAll();

        securityService.autoLogin(username, password);
        verifyAll();
    }

    private void replayAll() {
        PowerMock.replay(SecurityService.class, SecurityContextHolder.class, UsernamePasswordAuthenticationToken.class, authenticationManagerMock,
                authenticationTrustResolverMock, userDetailsServiceMock, securityContextMock, authenticationMock, userDetailsMock, usernamePasswordAuthenticationTokenMock);
    }

    private void verifyAll() {
        PowerMock.verify(SecurityService.class, SecurityContextHolder.class, UsernamePasswordAuthenticationToken.class, authenticationManagerMock,
                authenticationTrustResolverMock, userDetailsServiceMock, securityContextMock, authenticationMock, userDetailsMock, usernamePasswordAuthenticationTokenMock);
    }

    private void expectGetAuthenticationFromSecurityContext(Authentication authentication) {
        EasyMock.expect(SecurityContextHolder.getContext()).andReturn(securityContextMock);
        EasyMock.expect(securityContextMock.getAuthentication()).andReturn(authentication);
    }

    private void expectGetPrincipalFromAuthentication(Object object) {
        EasyMock.expect(authenticationMock.getPrincipal()).andReturn(object);
    }

    private void expectIsAnonymousCheck(boolean value) {
        EasyMock.expect(authenticationTrustResolverMock.isAnonymous(authenticationMock))
                .andReturn(value);
    }

    private void expectGetAuthoritiesFromAuthentication(List authorities) {
        EasyMock.expect(authenticationMock.getAuthorities()).andReturn(authorities);
    }

    private void expectLoadUserByUsername(String username) {
        EasyMock.expect(userDetailsServiceMock.loadUserByUsername(username))
                .andReturn(userDetailsMock);
    }

    private void expectGetAuthoritiesFromUserDetails(List authorities) {
        EasyMock.expect(userDetailsMock.getAuthorities()).andReturn(authorities);
    }

    private void expectNewUsernamePasswordAuthenticationToken(String password, List<GrantedAuthority> authorities) throws Exception {
        PowerMock.expectNew(UsernamePasswordAuthenticationToken.class, userDetailsMock, password, authorities)
                .andReturn(usernamePasswordAuthenticationTokenMock);
    }

    private void expectAuthenticateWithAuthenticationManager() {
        EasyMock.expect(authenticationManagerMock.authenticate(usernamePasswordAuthenticationTokenMock))
                .andReturn(usernamePasswordAuthenticationTokenMock);
    }

    private void expectIsAuthenticatedCheckOnUsernamePasswordAuthenticationToken(boolean isAuthenticated) {
        EasyMock.expect(usernamePasswordAuthenticationTokenMock.isAuthenticated()).andReturn(isAuthenticated);
    }

    private void expectSetAuthentication() {
        EasyMock.expect(SecurityContextHolder.getContext()).andReturn(securityContextMock);
        securityContextMock.setAuthentication(usernamePasswordAuthenticationTokenMock);
        EasyMock.expectLastCall();
    }
}
