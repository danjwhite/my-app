package com.example.myapp.dao;

import com.example.myapp.domain.Note;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class NoteDaoImpl implements INoteDao {

    private SessionFactory sessionFactory;

    @Autowired
    public NoteDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public Note findById(long id) {
        return currentSession().get(Note.class, id);
    }


    @Override
    public List<Note> findRecent() {
        return (List<Note>) noteCriteria().setMaxResults(10).list();
    }

    @Override
    public List<Note> findAll() {
        return (List<Note>) noteCriteria().list();
    }

    @Override
    public Note add(Note note) {

        note.setCreatedAt(new Date());

        Long id = (Long) currentSession().save(note);
        note.setId(id);

        return note;
    }

    @Override
    public Note update(Note note) {
        currentSession().update(note);

        return note;
    }

    @Override
    public void delete(long id) {
        currentSession().delete(findById(id));
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    private Criteria noteCriteria() {
        return currentSession().createCriteria(Note.class)
                .addOrder(Order.desc("id"));
    }
}
