package com.example.myapp.config;

import com.example.myapp.dao.INoteDao;
import com.example.myapp.service.INoteService;
import com.example.myapp.web.NoteController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BeanConfig.class, H2DataConfig.class})
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
