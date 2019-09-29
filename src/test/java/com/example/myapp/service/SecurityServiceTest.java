package com.example.myapp.service;

import com.example.myapp.dao.UserDaoImpl;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@TestPropertySource("classpath:application-test.properties")
@PrepareForTest(SecurityContextHolder.class)
public class SecurityServiceTest {

    private SecurityServiceImpl securityService;

    @Mock
    private EntityManager entityManagerMock;

    @Before
    public void setUp() {
        securityService = new SecurityServiceImpl();

        // Set the AuthenticationManager for SecurityServiceImpl.
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        securityService.setAuthenticationManager(authenticationManager);

        // Set the AuthenticationTrustResolver for SecurityServiceImpl.
        AuthenticationTrustResolverImpl authenticationTrustResolver = new AuthenticationTrustResolverImpl();
        securityService.setAuthenticationTrustResolver(authenticationTrustResolver);

        // Create UserDaoImpl with EntityManager for UserDetailsServiceImpl used in SecurityServiceImpl.
        UserDaoImpl userDao = new UserDaoImpl();
        userDao.setEntityManager(entityManagerMock);

        // Create UserDetailsServiceImpl for SecurityServiceImpl.
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setUserDao(userDao);
        securityService.setUserDetailsService(userDetailsService);
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

    @Test
    public void testAutoLogin() {

        User user = new User();
        user.setUsername("user");
        user.setPassword("password");

        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setType(RoleType.ROLE_USER);
        roles.add(role);
        user.setRoles(roles);

        // Set up mocks.
        CriteriaBuilder criteriaBuilderMock = mock(CriteriaBuilder.class);
        CriteriaQuery criteriaQueryMock1 = mock(CriteriaQuery.class);
        CriteriaQuery criteriaQueryMock2 = mock(CriteriaQuery.class);
        CriteriaQuery criteriaQueryMock3 = mock(CriteriaQuery.class);

        Root rootMock = mock(Root.class);
        Predicate predicateMock = mock(Predicate.class);
        Path pathMock = mock(Path.class);

        TypedQuery typedQueryMock = mock(TypedQuery.class);

        // Mock methods.
        when(entityManagerMock.getCriteriaBuilder()).thenReturn(criteriaBuilderMock);
        when(criteriaBuilderMock.createQuery(any())).thenReturn(criteriaQueryMock1);
        when(criteriaQueryMock1.from(User.class)).thenReturn(rootMock);

        when(criteriaQueryMock1.select(rootMock)).thenReturn(criteriaQueryMock2);
        when(criteriaQueryMock2.where(predicateMock)).thenReturn(criteriaQueryMock3);
        when(criteriaBuilderMock.equal(eq(pathMock), anyObject())).thenReturn(predicateMock);
        when(rootMock.get(anyString())).thenReturn(pathMock);

        when(entityManagerMock.createQuery(criteriaQueryMock1)).thenReturn(typedQueryMock);
        when(typedQueryMock.getSingleResult()).thenReturn(user);

        securityService.autoLogin("user", "password");

        // Expected principal
        UserDetails expectedPrincipal = org.springframework.security.core.userdetails.User.withUsername("user")
                .password("password")
                .authorities("ROLE_USER").build();

        UserDetails actualPrincipal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        assertEquals(expectedPrincipal, actualPrincipal);
    }
}
