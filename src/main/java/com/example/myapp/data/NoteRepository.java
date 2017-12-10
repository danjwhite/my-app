package com.example.myapp.data;

import java.util.List;

import com.example.myapp.domain.Note;

public interface NoteRepository {

    List<Note> findRecentNotes();

    List<Note> findNotes(long max, int count);

    Note findOne(long id);

    void save(Note note);
}
