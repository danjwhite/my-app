package com.example.myapp.builder.entity;

import com.example.myapp.builder.AbstractEntityBuilder;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;

import javax.persistence.EntityManager;
import java.util.Date;

public class NoteBuilder extends AbstractEntityBuilder<Note> {

    private NoteBuilder(EntityManager entityManager) {
        super(new Note(), entityManager);
    }

    public static NoteBuilder givenNote() {
        return new NoteBuilder(null);
    }

    public static NoteBuilder givenNote(EntityManager entityManager) {
        return new NoteBuilder(entityManager);
    }

    public NoteBuilder withId(Long id) {
        getObject().setId(id);
        return this;
    }

    public NoteBuilder withCreatedAt(Date createdAt) {
        getObject().setCreatedAt(createdAt);
        return this;
    }

    public NoteBuilder withUser(User user) {
        getObject().setUser(user);
        return this;
    }

    public NoteBuilder withTitle(String title) {
        getObject().setTitle(title);
        return this;
    }

    public NoteBuilder withBody(String body) {
        getObject().setBody(body);
        return this;
    }
}
