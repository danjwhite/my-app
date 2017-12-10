package com.example.myapp.data;

import java.util.List;

import com.example.myapp.domain.Note;

public interface NoteRepository {

    List<Note> findNotes(int count);

    Note findOne(long id);

    void save(Note note);
}
