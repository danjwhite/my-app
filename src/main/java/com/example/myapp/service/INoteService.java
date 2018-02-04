package com.example.myapp.service;

import com.example.myapp.domain.Note;

import java.util.List;

public interface INoteService {

    List<Note> findAll();

    List<Note> findRecent();

    Note findById(long id);

    Note add(Note note);

    Note update(Note note);

    void delete(Note note);

    long count();
}
