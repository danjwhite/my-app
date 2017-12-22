package com.example.myapp.config;

import com.example.myapp.dao.INoteDao;
import com.example.myapp.service.INoteService;
import com.example.myapp.web.NoteController;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = {BeanConfig.class})
public class BeanConfigTest {

    @Autowired
    private INoteDao noteDao;

    @Autowired
    private INoteService noteService;

    @Autowired
    private NoteController noteController;

    @Test
    public void createsNoteDao() {
        assertNotNull(noteDao);
    }

    @Test
    public void createsNoteService() {
        assertNotNull(noteService);
    }

    @Test
    public void createsNoteController() {
        assertNotNull(noteController);
    }
}
