package com.example.myapp.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@RunWith(PowerMockRunner.class)
@TestPropertySource("classpath:application-test.properties")
@PrepareForTest(SecurityContextHolder.class)
public class SecurityServiceTest {

    private SecurityServiceImpl securityService;

    @Before
    public void setUp() {
        securityService = new SecurityServiceImpl();

        AuthenticationTrustResolverImpl authenticationTrustResolver = new AuthenticationTrustResolverImpl();
        Field field = ReflectionUtils.findField(SecurityServiceImpl.class, "authenticationTrustResolver");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, securityService, authenticationTrustResolver);
    }

    @Test
    public void testFindPrincipal() {
        SecurityContext securityContextMock = mock(SecurityContext.class);
        Authentication authenticationMock = mock(Authentication.class);

        PowerMockito.mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContextMock);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);

        UserDetails expectedPrincipal = org.springframework.security.core.userdetails.User.withUsername("user")
                .password("password")
                .authorities("ROLE_USER").build();

        when(authenticationMock.getPrincipal()).thenReturn(expectedPrincipal);

        UserDetails actualPrincipal = securityService.getPrincipal();

        assertEquals(expectedPrincipal, actualPrincipal);
    }

    @Test
    public void testIsCurrentAuthenticationAnonymousWhenTrue() {
        SecurityContext securityContextMock = mock(SecurityContext.class);

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
        Authentication authentication = new AnonymousAuthenticationToken("anonymous", "anonymous", authorities);

        PowerMockito.mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContextMock);
        when(securityContextMock.getAuthentication()).thenReturn(authentication);

        assertTrue(securityService.isCurrentAuthenticationAnonymous());
    }

    @Test
    public void testIsCurrentAuthenticationAnonymousWhenFalse() {
        SecurityContext securityContextMock = mock(SecurityContext.class);

        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password");

        PowerMockito.mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContextMock);
        when(securityContextMock.getAuthentication()).thenReturn(authentication);

        assertFalse(securityService.isCurrentAuthenticationAnonymous());
    }
}
