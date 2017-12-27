package com.example.myapp.service;

import com.example.myapp.domain.Note;

import java.util.List;

public interface INoteService {

    List<Note> findAll();

    List<Note> findRecent(int count);

    Note findOne(long id);

    Note add(Note note);

    Note update(Note note);

    void delete(long id);

    long count();
}
