package com.example.myapp.dao;

import com.example.myapp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class UserDaoImpl implements IUserDao {

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User findById(long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User findByUsername(String username) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("username"), username));
        User user = null;
        try {
            user = entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            // Do nothing
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);
        TypedQuery<User> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getResultList();
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public User add(User user) {
        entityManager.persist(user);
        entityManager.flush();

        return user;
    }

    @Override
    public User update(User user) {
        return entityManager.merge(user);
    }

    @Override
    public void delete(User user) {
        entityManager.remove(user);
    }
}
