package com.example.myapp.dao;

import com.example.myapp.domain.Role;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDaoImpl implements IRoleDao {

    private SessionFactory sessionFactory;

    @Autowired
    public RoleDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Role findById(long id) {
        return currentSession().get(Role.class, id);
    }

    @Override
    public Role findByType(String type) {
        return currentSession().get(Role.class, type);
    }

    @Override
    public List<Role> findAll() {
        return (List<Role>) roleCriteria().list();
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    private Criteria roleCriteria() {
        return currentSession().createCriteria(Role.class)
                .addOrder(Order.desc("id"));
    }
}
