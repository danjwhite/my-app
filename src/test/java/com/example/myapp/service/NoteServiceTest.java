package com.example.myapp.service;

import static org.junit.Assert.*;

import com.example.myapp.domain.Note;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NoteServiceTest {

    @Autowired
    INoteService noteService;

    @Test
    @Transactional
    public void testCount() {
        assertEquals(12, noteService.count());
    }

    @Test
    @Transactional
    public void testFindAll() {
        assertEquals(12, noteService.findAll().size());
    }

    @Test
    @Transactional
    public void testFindRecent() {
        assertEquals(10, noteService.findRecent().size());
    }

    @Test
    @Transactional
    public void testFindOne() {
        Note note = noteService.findById(1L);
        assertEquals(1L, note.getId().longValue());
        assertEquals("Title", note.getTitle());
        assertEquals("Body", note.getBody());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testAdd() {
        assertEquals(12, noteService.count());

        Note newNote = new Note(null, null, "Title", "Body");
        Note savedNote = noteService.add(newNote);

        assertEquals(13, noteService.count());
        assertEquals(13L, savedNote.getId().longValue());
        assertNotNull(savedNote.getCreatedAt());
        assertEquals("Title", savedNote.getTitle());
        assertEquals("Body", savedNote.getBody());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testUpdate() {
        assertEquals(12, noteService.count());

        Note originalNote = noteService.findById(1L);
        Date originalCreatedAt = originalNote.getCreatedAt();
        String originalTitle = originalNote.getTitle();
        String originalBody = originalNote.getBody();

        originalNote.setTitle("New Title");
        originalNote.setBody("New Body");
        noteService.update(originalNote);

        Note updatedNote = noteService.findById(1L);

        assertEquals(12, noteService.count());
        assertEquals(1L, updatedNote.getId().longValue());
        assertEquals(originalCreatedAt, updatedNote.getCreatedAt());
        assertNotEquals(originalTitle, updatedNote.getTitle());
        assertNotEquals(originalBody, updatedNote.getBody());
    }

    @Test
    @Transactional
    @SuppressWarnings("Duplicates")
    public void testDelete() {
        assertEquals(12, noteService.count());
        assertNotNull(noteService.findById(1L));

        noteService.delete(1L);

        assertEquals(11, noteService.count());
        assertNull(noteService.findById(1L));
    }
}
