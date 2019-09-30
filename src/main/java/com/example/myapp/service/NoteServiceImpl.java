package com.example.myapp.service;

import com.example.myapp.dao.NoteRepository;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@Service
public class NoteServiceImpl implements INoteService {

    private NoteRepository noteRepository;

    private IUserService userService;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, IUserService userService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Note> findAll() {
        User user = userService.getLoggedInUser();

        return noteRepository.findAllByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Note> findRecent() {
        User user = userService.getLoggedInUser();

        return noteRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @PostAuthorize("returnObject == null || returnObject.user.username == authentication.name")
    @Transactional(readOnly = true)
    @Override
    public Note findById(long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found for id: " + id));
    }

    @Transactional
    @Override
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
    @Override
    public Note update(NoteDto noteDto) {

        Note note = findById(noteDto.getNoteId());
        note.setTitle(noteDto.getTitle());
        note.setBody(noteDto.getBody());

        return noteRepository.save(note);
    }

    @PreAuthorize("#note.user.username == authentication.name")
    @Transactional
    @Override
    public void delete(Note note) {
        noteRepository.delete(note);
    }

    @Transactional(readOnly = true)
    @Override
    public long count() {
        User user = userService.getLoggedInUser();

        return noteRepository.countByUserId(user.getId());
    }
}
