package com.example.myapp.config;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {H2DataConfig.class})
public class H2DataConfigTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LocalSessionFactoryBean localSessionFactoryBean;

    @Autowired
    private HibernateTransactionManager hibernateTransactionManager;

    @Test
    public void createsDataSource() {
        assertNotNull(dataSource);
    }

    @Test
    public void createsLocalSessionFactoryBean() {
        assertNotNull(localSessionFactoryBean);
    }

    @Test
    public void createsHibernateTransactionManager() {
        assertNotNull(hibernateTransactionManager);
    }

}
