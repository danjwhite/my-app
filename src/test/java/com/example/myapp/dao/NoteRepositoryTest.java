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
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userDao;

    @Test
    @Transactional
    @Rollback
    public void countShouldReturnExpectedResult() {

        // Count all notes and assert expectations.
        Assert.assertEquals(24, noteRepository.count());
    }

    @Test
    @Transactional
    @Rollback
    public void findAllByUserIdShouldReturnExpectedResult() {

        // Find all notes for specified user and assert expectations.
        Assert.assertEquals(12, noteRepository.findAllByUserId(1).size());
    }

    // TODO: Update test to assert ordering.
    @Test
    @Transactional
    @Rollback
    public void findTop10ByUserIdOrderByCreatedAtDescShouldReturnTenMostRecentNotes() {

        // Find recent notes for specified user and assert expectations.
        Assert.assertEquals(10, noteRepository.findTop10ByUserIdOrderByCreatedAtDesc(1).size());
    }

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void findByIdShouldReturnExpectedResult() throws Exception {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse("2018-02-02 00:00:00");

        // Find specific note by id and assert expectations.
        Optional<Note> note = noteRepository.findById(1L);
        Assert.assertTrue(note.isPresent());
        Assert.assertEquals(1L, note.get().getId().longValue());
        Assert.assertEquals(date, note.get().getCreatedAt());
        Assert.assertEquals("mjones", note.get().getUser().getUsername());
        Assert.assertEquals("Title", note.get().getTitle());
        Assert.assertEquals("Body", note.get().getBody());
    }

    // TODO: Break down into smaller tests.
    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void saveShouldSetExpectedValuesForAdd() {

        Assert.assertEquals(24, noteRepository.count());

        // Get user for new note.
        User user = userDao.findByUsername("mjones");

        // Create, add, and retrieve new note
        Note newNote = new Note(new Date(), user, "Title", "Body");
        noteRepository.save(newNote);

        Optional<Note> savedNote = noteRepository.findById(25L);

        // Assert expectations
        Assert.assertTrue(savedNote.isPresent());
        Assert.assertEquals(25, noteRepository.count());
        Assert.assertEquals(25L, savedNote.get().getId().longValue());
        Assert.assertNotNull(savedNote.get().getCreatedAt());
        Assert.assertEquals("mjones", savedNote.get().getUser().getUsername());
        Assert.assertEquals("Title", savedNote.get().getTitle());
        Assert.assertEquals("Body", savedNote.get().getBody());
    }

    // TODO: Break down into smaller tests.
    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void saveShouldSetExpectedValuesForUpdate() {
        Assert.assertEquals(24, noteRepository.count());

        Optional<Note> originalNote = noteRepository.findById(1L);
        Assert.assertTrue(originalNote.isPresent());

        Date originalCreatedAt = originalNote.get().getCreatedAt();
        String originalUsername = originalNote.get().getUser().getUsername();
        String originalTitle = originalNote.get().getTitle();
        String originalBody = originalNote.get().getBody();

        originalNote.get().setTitle("New Title");
        originalNote.get().setBody("New Body");
        noteRepository.save(originalNote.get());

        Optional<Note> updatedNote = noteRepository.findById(1L);

        Assert.assertTrue(updatedNote.isPresent());
        Assert.assertEquals(24, noteRepository.count());
        Assert.assertEquals(1L, updatedNote.get().getId().longValue());
        Assert.assertEquals(originalCreatedAt, updatedNote.get().getCreatedAt());
        Assert.assertEquals(originalUsername, updatedNote.get().getUser().getUsername());
        Assert.assertNotEquals(originalTitle, updatedNote.get().getTitle());
        Assert.assertNotEquals(originalBody, updatedNote.get().getBody());
    }

    @Test
    @Transactional
    @Rollback
    @SuppressWarnings("Duplicates")
    public void deleteShouldDeleteExpectedNote() {

        // Get note to delete.
        Optional<Note> note = noteRepository.findById(1L);
        Assert.assertTrue(note.isPresent());

        Assert.assertEquals(24, noteRepository.count());
        Assert.assertNotNull(note);

        noteRepository.delete(note.get());

        Assert.assertEquals(23, noteRepository.count());
        Assert.assertFalse(noteRepository.findById(1L).isPresent());
    }

}
