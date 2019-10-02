package com.example.myapp.service;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OldRoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    @Transactional
    public void testFindAll() {
        assertEquals(2, roleService.findAll().size());
    }

    @Test
    @Transactional
    public void testFindById() {
        Role expectedRole = new Role();
        expectedRole.setId(1L);
        expectedRole.setType(RoleType.ROLE_USER);

        assertEquals(expectedRole, roleService.findById(1L));
    }

    @Test
    @Transactional
    public void testFindByType() {
        Role expectedRole = new Role();
        expectedRole.setId(1L);
        expectedRole.setType(RoleType.ROLE_USER);

        assertEquals(expectedRole, roleService.findByType(RoleType.ROLE_USER));
    }
}
