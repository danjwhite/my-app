package com.example.myapp.service;

import static org.junit.Assert.*;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserRegistrationDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SecurityServiceTest {

    @Autowired
    private ISecurityService securityService;

    @Before
    public void setup() {

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("mjones")
                .password("$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS")
                .authorities("ROLE_USER", "ROLE_ADMIN").build();

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @Transactional
    public void testFindLoggedInUsername() {

        assertEquals("mjones", securityService.findLoggedInUsername());
    }
}
