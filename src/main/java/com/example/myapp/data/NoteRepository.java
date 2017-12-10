package com.example.myapp.data;

import java.util.List;

import com.example.myapp.domain.Note;

public interface NoteRepository {

    List<Note> findNotes();

    List<Note> findRecentNotes(int count);

    Note findOne(long id);

    Long save(Note note);
}
