package com.example.myapp.dao;

import com.example.myapp.domain.Note;

import java.util.List;

public interface INoteDao {

    List<Note> findAll(long userId);

    List<Note> findRecent(long userId);

    Note findById(long id);

    Note add(Note note);

    Note update(Note note);

    void delete(long id);

    long count();

    long count(long userId);
}
