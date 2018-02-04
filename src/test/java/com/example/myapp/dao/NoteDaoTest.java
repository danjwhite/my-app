package com.example.myapp.dao;

import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NoteDaoTest {

    @Autowired
    private INoteDao noteDao;

    @Autowired
    private IUserDao userDao;

    @Test
    @Transactional
    public void testCount() {

        // Count all notes and assert expectations.
        assertEquals(24, noteDao.count());
    }

    @Test
    @Transactional
    public void testFindAll() {

        // Find all notes for specified user and assert expectations.
        assertEquals(12, noteDao.findAll(1).size());
    }

    @Test
    @Transactional
    public void testFindRecent() {

        // Find recent notes for specified user and assert expectations.
        assertEquals(10, noteDao.findRecent(1).size());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testFindOne() throws Exception {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse("2018-02-02 00:00:00");

        // Find specific note by id and assert expectations.
        Note note = noteDao.findById(1L);
        assertEquals(1L, note.getId().longValue());
        assertEquals(date, note.getCreatedAt());
        assertEquals("mjones", note.getUser().getUsername());
        assertEquals("Title", note.getTitle());
        assertEquals("Body", note.getBody());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testAdd() {

        assertEquals(24, noteDao.count());

        // Get user for new note.
        User user = userDao.findByUsername("mjones");

        // Create, add, and retrieve new note
        Note newNote = new Note(new Date(), user, "Title", "Body");
        noteDao.add(newNote);

        Note savedNote = noteDao.findById(25L);

        // Assert expectations
        assertEquals(25, noteDao.count());
        assertEquals(25L, savedNote.getId().longValue());
        assertNotNull(savedNote.getCreatedAt());
        assertEquals("mjones", savedNote.getUser().getUsername());
        assertEquals("Title", savedNote.getTitle());
        assertEquals("Body", savedNote.getBody());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testUpdate() {
        assertEquals(24, noteDao.count());

        Note originalNote = noteDao.findById(1L);
        Date originalCreatedAt = originalNote.getCreatedAt();
        String originalUsername = originalNote.getUser().getUsername();
        String originalTitle = originalNote.getTitle();
        String originalBody = originalNote.getBody();

        originalNote.setTitle("New Title");
        originalNote.setBody("New Body");
        noteDao.update(originalNote);

        Note updatedNote = noteDao.findById(1L);

        assertEquals(24, noteDao.count());
        assertEquals(1L, updatedNote.getId().longValue());
        assertEquals(originalCreatedAt, updatedNote.getCreatedAt());
        assertEquals(originalUsername, updatedNote.getUser().getUsername());
        assertNotEquals(originalTitle, updatedNote.getTitle());
        assertNotEquals(originalBody, updatedNote.getBody());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testDelete() {
        assertEquals(24, noteDao.count());
        assertNotNull(noteDao.findById(1L));

        noteDao.delete(1L);

        assertEquals(23, noteDao.count());
        assertNull(noteDao.findById(1L));
    }

}
