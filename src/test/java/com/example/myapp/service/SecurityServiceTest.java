package com.example.myapp.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

@RunWith(PowerMockRunner.class)
@TestPropertySource("classpath:application-test.properties")
@PrepareForTest(SecurityContextHolder.class)
public class SecurityServiceTest {

    private SecurityServiceImpl securityService;

    @Before
    public void setUp() {
        securityService = new SecurityServiceImpl();

        // Set the AuthenticationManager for SecurityServiceImpl
        List<AuthenticationProvider> authenticationProviders = Arrays.asList(new TestingAuthenticationProvider());
        AuthenticationManager authenticationManager = new ProviderManager(authenticationProviders);
        securityService.setAuthenticationManager(authenticationManager);

        // Set the AuthenticationTrustResolver for SecurityServiceImpl
        AuthenticationTrustResolverImpl authenticationTrustResolver = new AuthenticationTrustResolverImpl();
        securityService.setAuthenticationTrustResolver(authenticationTrustResolver);
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
