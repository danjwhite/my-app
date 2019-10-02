package com.example.myapp.repository;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional
    @Rollback
    public void findAllShouldReturnExpectedResult() {
        List<Role> roles = (List<Role>) roleRepository.findAll();

        Assert.assertEquals(RoleType.values().length, roles.size());

        Set<RoleType> roleTypes = roles.stream().map(Role::getType).collect(Collectors.toSet());
        Assert.assertEquals(new HashSet<>(Arrays.asList(RoleType.values())), roleTypes);
    }

    @Test
    @Transactional
    @Rollback
    public void findByIdShouldReturnExpectedResult() {
        Role expectedRole = new Role();
        expectedRole.setId(1L);
        expectedRole.setType(RoleType.ROLE_USER);

        Optional<Role> result = roleRepository.findById(1L);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(expectedRole, result.get());
    }

    @Test
    @Transactional
    @Rollback
    public void findByTypeShouldReturnExpectedResult() {
        Role expectedRole = new Role();
        expectedRole.setId(1L);
        expectedRole.setType(RoleType.ROLE_USER);

        Assert.assertEquals(expectedRole, roleRepository.findByType(RoleType.ROLE_USER));
    }
}
