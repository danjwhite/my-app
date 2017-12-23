package com.example.myapp.dao;

import com.example.myapp.domain.Note;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public class NoteDaoImpl implements INoteDao {

    private SessionFactory sessionFactory;

    @Autowired
    public NoteDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // TODO: Add tests for this class.

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public Note findOne(long id) {
        return currentSession().get(Note.class, id);
    }

    @Override
    public List<Note> findRecent(int count) {
        return (List<Note>) noteCriteria().setMaxResults(count).list();
    }

    @Override
    public List<Note> findAll() {
        return (List<Note>) noteCriteria().list();
    }

    @Override
    public Note save(Note note) {
        Serializable id = currentSession().save(note);

        return new Note(
                (Long) id,
                note.getCreatedAt(),
                note.getTitle(),
                note.getBody()
        );
    }

    @Override
    public void delete(long id) {
        currentSession().delete(findOne(id));
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    private Criteria noteCriteria() {
        return currentSession().createCriteria(Note.class)
                .addOrder(Order.desc("id"));
    }
}
