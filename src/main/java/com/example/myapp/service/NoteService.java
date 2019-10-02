package com.example.myapp.service;

import com.example.myapp.repository.NoteRepository;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;

    public NoteService(NoteRepository noteRepository, UserService userService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Note> findAll() {
        User user = userService.getLoggedInUser();

        return noteRepository.findAllByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public List<Note> findRecent() {
        User user = userService.getLoggedInUser();

        return noteRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @PostAuthorize("returnObject.user.username == authentication.name")
    @Transactional(readOnly = true)
    public Note findById(long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found for id: " + id));
    }

    @Transactional
    public Note add(NoteDto noteDto) {
        
        Note note = new Note();
        note.setCreatedAt(new Date());
        note.setUser(userService.getLoggedInUser());
        note.setTitle(noteDto.getTitle());
        note.setBody(noteDto.getBody());

        return noteRepository.save(note);
    }

    @PreAuthorize("#noteDto.username == authentication.name")
    @Transactional
    public Note update(NoteDto noteDto) {

        Note note = findById(noteDto.getNoteId());
        note.setTitle(noteDto.getTitle());
        note.setBody(noteDto.getBody());

        return noteRepository.save(note);
    }

    @PreAuthorize("#note.user.username == authentication.name")
    @Transactional
    public void delete(Note note) {
        noteRepository.delete(note);
    }

    @Transactional(readOnly = true)
    public long count() {
        User user = userService.getLoggedInUser();

        return noteRepository.countByUserId(user.getId());
    }
}
