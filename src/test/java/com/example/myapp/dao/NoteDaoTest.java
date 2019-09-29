package com.example.myapp.dao;

import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class NoteDaoTest {

    @Autowired
    private INoteDao noteDao;

    @Autowired
    private IUserDao userDao;

    @Test
    @Transactional
    @Rollback
    public void countShouldReturnExpectedResult() {

        // Count all notes and assert expectations.
        Assert.assertEquals(24, noteDao.count());
    }

    @Test
    @Transactional
    @Rollback
    public void findAllShouldReturnAllNotes() {

        // Find all notes for specified user and assert expectations.
        Assert.assertEquals(12, noteDao.findAll(1).size());
    }

    @Test
    @Transactional
    @Rollback
    public void findRecentShouldReturnTenMostRecentNotes() {

        // Find recent notes for specified user and assert expectations.
        Assert.assertEquals(10, noteDao.findRecent(1).size());
    }

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void findByIdShouldReturnExpectedResult() throws Exception {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse("2018-02-02 00:00:00");

        // Find specific note by id and assert expectations.
        Note note = noteDao.findById(1L);
        Assert.assertEquals(1L, note.getId().longValue());
        Assert.assertEquals(date, note.getCreatedAt());
        Assert.assertEquals("mjones", note.getUser().getUsername());
        Assert.assertEquals("Title", note.getTitle());
        Assert.assertEquals("Body", note.getBody());
    }

    // TODO: Break down into smaller tests.
    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void addShouldSetExpectedFieldValues() {

        Assert.assertEquals(24, noteDao.count());

        // Get user for new note.
        User user = userDao.findByUsername("mjones");

        // Create, add, and retrieve new note
        Note newNote = new Note(new Date(), user, "Title", "Body");
        noteDao.add(newNote);

        Note savedNote = noteDao.findById(25L);

        // Assert expectations
        Assert.assertEquals(25, noteDao.count());
        Assert.assertEquals(25L, savedNote.getId().longValue());
        Assert.assertNotNull(savedNote.getCreatedAt());
        Assert.assertEquals("mjones", savedNote.getUser().getUsername());
        Assert.assertEquals("Title", savedNote.getTitle());
        Assert.assertEquals("Body", savedNote.getBody());
    }

    // TODO: Break down into smaller tests.
    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void updateShouldSetExpectedFieldValues() {
        Assert.assertEquals(24, noteDao.count());

        Note originalNote = noteDao.findById(1L);
        Date originalCreatedAt = originalNote.getCreatedAt();
        String originalUsername = originalNote.getUser().getUsername();
        String originalTitle = originalNote.getTitle();
        String originalBody = originalNote.getBody();

        originalNote.setTitle("New Title");
        originalNote.setBody("New Body");
        noteDao.update(originalNote);

        Note updatedNote = noteDao.findById(1L);

        Assert.assertEquals(24, noteDao.count());
        Assert.assertEquals(1L, updatedNote.getId().longValue());
        Assert.assertEquals(originalCreatedAt, updatedNote.getCreatedAt());
        Assert.assertEquals(originalUsername, updatedNote.getUser().getUsername());
        Assert.assertNotEquals(originalTitle, updatedNote.getTitle());
        Assert.assertNotEquals(originalBody, updatedNote.getBody());
    }

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void deleteShouldDeleteExpectedNote() {

        // Get note to delete.
        Note note = noteDao.findById(1L);

        Assert.assertEquals(24, noteDao.count());
        Assert.assertNotNull(note);

        noteDao.delete(note);

        Assert.assertEquals(23, noteDao.count());
        Assert.assertNull(noteDao.findById(1L));
    }

}
