package com.example.myapp.service;

import com.example.myapp.dao.INoteDao;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class NoteServiceImpl implements INoteService {

    private INoteDao noteDao;

    private IUserService userService;

    @Autowired
    public NoteServiceImpl(INoteDao noteDao, IUserService userService) {
        this.noteDao = noteDao;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Note> findAll() {
        User user = userService.getLoggedInUser();

        return noteDao.findAll(user.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Note> findRecent() {
        User user = userService.getLoggedInUser();

        return noteDao.findRecent(user.getId());
    }

    @PostAuthorize("returnObject == null || returnObject.user.username == authentication.name")
    @Transactional(readOnly = true)
    @Override
    public Note findById(long id) {
        return noteDao.findById(id);
    }

    @Transactional
    @Override
    public Note add(NoteDto noteDto) {
        
        Note note = new Note();
        note.setCreatedAt(new Date());
        note.setUser(userService.getLoggedInUser());
        note.setTitle(noteDto.getTitle());
        note.setBody(noteDto.getBody());

        return noteDao.add(note);
    }

    @PreAuthorize("#noteDto.username == authentication.name")
    @Transactional
    @Override
    public Note update(NoteDto noteDto) {

        Note note = findById(noteDto.getNoteId());
        note.setTitle(noteDto.getTitle());
        note.setBody(noteDto.getBody());

        return noteDao.update(note);
    }

    @PreAuthorize("#note.user.username == authentication.name")
    @Transactional
    @Override
    public void delete(Note note) {
        noteDao.delete(note);
    }

    @Transactional(readOnly = true)
    @Override
    public long count() {
        User user = userService.getLoggedInUser();

        return noteDao.count(user.getId());
    }
}
