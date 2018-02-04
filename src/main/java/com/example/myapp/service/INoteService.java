package com.example.myapp.service;

import com.example.myapp.domain.Note;
import com.example.myapp.dto.NoteDto;

import java.util.List;

public interface INoteService {

    List<Note> findAll();

    List<Note> findRecent();

    Note findById(long id);

    Note add(NoteDto noteDto);

    Note update(NoteDto noteDto);

    void delete(Note note);

    long count();
}
