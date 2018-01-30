package com.example.myapp.dao;

import com.example.myapp.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

@Repository
public class NoteDaoImpl implements INoteDao {

    private EntityManager entityManager;

    @Autowired
    public NoteDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public Note findById(long id) {
        return entityManager.find(Note.class, id);
    }


    @Override
    public List<Note> findRecent() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Note> criteriaQuery = criteriaBuilder.createQuery(Note.class);
        Root<Note> root = criteriaQuery.from(Note.class);
        criteriaQuery.select(root);
        TypedQuery<Note> typedQuery = entityManager.createQuery(criteriaQuery).setMaxResults(10);

        return typedQuery.getResultList();
    }

    @Override
    public List<Note> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Note> criteriaQuery = criteriaBuilder.createQuery(Note.class);
        Root<Note> root = criteriaQuery.from(Note.class);
        criteriaQuery.select(root);
        TypedQuery<Note> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getResultList();
    }

    @Override
    public Note add(Note note) {

        note.setCreatedAt(new Date());
        entityManager.persist(note);
        entityManager.flush();

        return note;
    }

    @Override
    public Note update(Note note) {
        return entityManager.merge(note);
    }

    @Override
    public void delete(long id) {
        entityManager.remove(findById(id));
    }
}
