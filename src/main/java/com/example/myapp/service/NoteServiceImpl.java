package com.example.myapp.service;

import com.example.myapp.dao.INoteDao;
import com.example.myapp.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class NoteServiceImpl implements INoteService {

    private INoteDao noteDao;

    @Autowired
    public NoteServiceImpl(INoteDao noteDao) {
        this.noteDao = noteDao;
    }

    // TODO: Add test for this method.
    @Transactional(readOnly = true)
    @Override
    public List<Note> findAll() {
        return noteDao.findAll();
    }

    // TODO: Add test for this method.
    @Transactional(readOnly = true)
    @Override
    public List<Note> findRecent() {
        return noteDao.findRecent();
    }

    // TODO: Add test for this method.
    @Transactional(readOnly = true)
    @Override
    public Note findOne(long id) {
        return noteDao.findOne(id);
    }

    // TODO: Add test for this method.
    @Transactional
    @Override
    public Note add(Note note) {
        return noteDao.add(note);
    }

    // TODO: Add test for this method.
    @Transactional
    @Override
    public Note update(Note note) {
        return noteDao.update(note);
    }

    // TODO: Add test for this method.
    @Transactional
    @Override
    public void delete(long id) {
        noteDao.delete(id);
    }

    // TODO: Add test for this method.
    @Transactional
    @Override
    public long count() {
        return noteDao.count();
    }
}
