package com.example.myapp.service;

import com.example.myapp.dao.INoteDao;
import com.example.myapp.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteServiceImpl implements INoteService {

    private INoteDao noteDao;

    @Autowired
    public NoteServiceImpl(INoteDao noteDao) {
        this.noteDao = noteDao;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Note> findAll() {
        return noteDao.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Note> findRecent(int count) {
        return noteDao.findRecent(count);
    }

    @Transactional(readOnly = true)
    @Override
    public Note findOne(long id) {
        return noteDao.findOne(id);
    }

    @Transactional
    @Override
    public Note save(Note note) {
        return noteDao.save(note);
    }

    @Transactional
    @Override
    public void delete(long id) {
        noteDao.delete(id);
    }

    @Transactional
    @Override
    public long count() {
        return noteDao.count();
    }
}
