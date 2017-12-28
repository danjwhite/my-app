package com.example.myapp.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.myapp.config.TestConfig;
import com.example.myapp.service.INoteService;

import org.junit.Before;
import org.junit.Test;


import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.myapp.domain.Note;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class NoteControllerTest {

    @Autowired
    private NoteController noteController;

    @Autowired
    private INoteService noteService;

    private MockMvc mockMvc;

    @Before
    public void setup() {

        // Setup MockMvc to use NoteController.
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(noteController).build();
    }

    @Test
    public void shouldShowAllNotes() throws Exception {

        // Create expected object.
        List<Note> expectedNotes = createNoteList(10);

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/notes/view/entries?display=all"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithoutRequestParameter() throws Exception {

        // Create expected object.
        List<Note> expectedNotes = createNoteList(10);

        // Perform GET request on MockMvc without request parameters and assert expectations.
        mockMvc.perform(get("/notes/view/entries?display=recent"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithRequestParameter() throws Exception {

        // Create expected object.
        List<Note> expectedNotes = createNoteList(10);

        // Perform GET request on MockMvc with request parameters and assert expectations.
        mockMvc.perform(get("/notes/view/entries?display=recent&maxResults=10"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void testGetNote() throws Exception {

        // Create expected object.
        Note expectedNote = noteService.findOne(1L);

        // Perform GET request on MockMvc with path variable and assert expectations.
        mockMvc.perform(get("/notes/view/entry?noteId=1"))
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", expectedNote));
    }

    @Test
    public void shouldShowNoteFormForAdding() throws Exception {

        // Perform GET request on MockMvc to add a note and assert expectations.
        mockMvc.perform(get("/notes/add"))
                .andExpect(view().name("noteForm"));
    }

    @Test
    public void shouldShowNoteFormForEditing() throws Exception {

        // Get the expected model attribute object.
        Note note = noteService.findOne(1L);

        // Get the expected properties of the attribute object.
        Date createdAt = note.getCreatedAt();
        String title = note.getTitle();
        String body = note.getBody();

        // Perform GET request on MockMvc to edit a note and assert expectations.
        mockMvc.perform(get("/notes/edit?noteId=1"))
                .andExpect(view().name("noteForm"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", hasProperty("id", is(1L))))
                .andExpect(model().attribute("note", hasProperty("createdAt", is(createdAt))))
                .andExpect(model().attribute("note", hasProperty("title", is(title))))
                .andExpect(model().attribute("note", hasProperty("body", is(body))));
    }

    @Test
    public void testAddNote() throws Exception {

        // Perform POST request to add a note on MockMvc and assert expectations.
        mockMvc.perform(post("/notes/add")
                .param("title", "Title")
                .param("body", "Body"))
                .andExpect(redirectedUrl("/notes/view/entry?noteId=11&confirmation=added"));
    }

    @Test
    public void testEditNote() throws Exception {

        // Perform POST request to edit a note on MockMvc and assert expectations.
        mockMvc.perform(post("/notes/edit?noteId=1")
                .param("id", "1")
                .param("title", "New title")
                .param("body", "New body"))
                .andExpect(redirectedUrl("/notes/view/entry?noteId=1&confirmation=edited"));
    }

    @Test
    public void testDeleteNote() throws Exception {

        // Assert note count before delete.
        assertEquals(11, noteService.count());

        // Perform GET request to delete a note on MockMvc and assert expectations.
        mockMvc.perform(get("/notes/delete?noteId=1"))
                .andExpect(redirectedUrl("/notes/view/entries"));

        // Assert note count after delete.
        assertEquals(10, noteService.count());
    }

    private List<Note> createNoteList(int count) {
        List<Note> notes = new ArrayList<Note>();

        for (int i = 0; i < count; i++) {
            notes.add(new Note(new Date(), "Title", "Body"));
        }

        return notes;
    }
}
