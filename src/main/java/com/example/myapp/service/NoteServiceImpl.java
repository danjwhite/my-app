package com.example.myapp.service;

import com.example.myapp.dao.INoteDao;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional(readOnly = true)
    @Override
    public Note findById(long id) {
        return noteDao.findById(id);
    }

    @Transactional
    @Override
    public Note add(Note note) {
        User user = userService.getLoggedInUser();
        note.setUser(user);
        note.setCreatedAt(new Date());

        return noteDao.add(note);
    }

    @Transactional
    @Override
    public Note update(Note note) {
        return noteDao.update(note);
    }

    @Transactional
    @Override
    public void delete(long id) {
        noteDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public long count() {
        User user = userService.getLoggedInUser();

        return noteDao.count(user.getId());
    }
}
