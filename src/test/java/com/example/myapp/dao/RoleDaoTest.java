package com.example.myapp.dao;

import com.example.myapp.domain.Role;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class RoleDaoTest {

    @Autowired
    private IRoleDao roleDao;

    @Test
    @Transactional
    @Rollback
    public void findAllShouldReturnExpectedResult() {
        Assert.assertEquals(2, roleDao.findAll().size());
    }

    @Test
    @Transactional
    @Rollback
    public void findByIdShouldReturnExpectedResult() {
        Role expectedRole = new Role();
        expectedRole.setId(1L);
        expectedRole.setType("ROLE_USER");

        Assert.assertEquals(expectedRole, roleDao.findById(1L));
    }

    @Test
    @Transactional
    @Rollback
    public void findByTypeShouldReturnExpectedResult() {
        Role expectedRole = new Role();
        expectedRole.setId(1L);
        expectedRole.setType("ROLE_USER");

        Assert.assertEquals(expectedRole, roleDao.findByType("ROLE_USER"));
    }
}
