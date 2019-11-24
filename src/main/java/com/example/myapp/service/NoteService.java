package com.example.myapp.service;

import com.example.myapp.domain.Note;
import com.example.myapp.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public Page<Note> findAllForUser(Pageable pageable, long userId) {
        return noteRepository.findAllByUserId(pageable, userId);
    }

    public Page<Note> searchForUser(Pageable pageable, long userId, String search) {
        return noteRepository.searchByUserId(pageable, userId, search);
    }

    public Note findForUser(long userId, UUID guid) {
        Note note = noteRepository.findByUserIdAndGuid(userId, guid);
        if (note == null) {
            throw new EntityNotFoundException(String.format("Note not found for userId: %d," +
                    " noteGuid: %s.", userId, guid.toString()));
        }

        return note;
    }

    public void save(Note note) {
        noteRepository.save(note);
    }

    public void delete(Note note) {
        noteRepository.delete(note);
    }
}
