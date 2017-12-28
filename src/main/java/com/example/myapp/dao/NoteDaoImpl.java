package com.example.myapp.dao;

import com.example.myapp.domain.Note;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Repository
public class NoteDaoImpl implements INoteDao {

    private SessionFactory sessionFactory;

    @Autowired
    public NoteDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // TODO: Add test for this method.
    @Override
    public long count() {
        return findAll().size();
    }

    // TODO: Add test for this method.
    @Override
    public Note findOne(long id) {
        return currentSession().get(Note.class, id);
    }

    // TODO: Add test for this method.
    @Override
    public List<Note> findRecent(int count) {
        return (List<Note>) noteCriteria().setMaxResults(count).list();
    }

    // TODO: Add test for this method.
    @Override
    public List<Note> findAll() {
        return (List<Note>) noteCriteria().list();
    }

    // TODO: Add test for this method.
    @Override
    public Note add(Note note) {

        note.setCreatedAt(new Date());

        Long id = (Long) currentSession().save(note);
        note.setId(id);

        return note;
    }

    // TODO: Add test for this method.
    @Override
    public Note update(Note note) {
        currentSession().update(note);

        return note;
    }

    // TODO: Add test for this method.
    @Override
    public void delete(long id) {
        currentSession().delete(findOne(id));
    }

    // TODO: Add test for this method.
    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    private Criteria noteCriteria() {
        return currentSession().createCriteria(Note.class)
                .addOrder(Order.desc("id"));
    }
}
