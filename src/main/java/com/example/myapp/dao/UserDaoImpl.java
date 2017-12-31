package com.example.myapp.dao;

import com.example.myapp.domain.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserDaoImpl implements IUserDao {

    private SessionFactory sessionFactory;

    @Autowired
    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public User findById(long id) {
        return currentSession().get(User.class, id);
    }

    @Override
    public User findByUsername(String username) {
        return currentSession().get(User.class, username);
    }

    @Override
    public List<User> findAll() {
        return (List<User>) userCriteria().list();
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public User add(User user) {
        Long id = (Long) currentSession().save(user);
        user.setId(id);

        return user;
    }

    @Override
    public User update(User user) {
        currentSession().update(user);

        return user;
    }

    @Override
    public void delete(String username) {
        currentSession().delete(findByUsername(username));
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    private Criteria userCriteria() {
        return currentSession().createCriteria(User.class)
                .addOrder(Order.desc("id"));
    }
}
