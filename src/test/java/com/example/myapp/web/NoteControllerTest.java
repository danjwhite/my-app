package com.example.myapp.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.myapp.config.TestConfig;
import com.example.myapp.service.INoteService;

import org.junit.Before;
import org.junit.Ignore;
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
        mockMvc.perform(get("/note/entries/all"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithoutRequestParameter() throws Exception {

        // Create expected object.
        List<Note> expectedNotes = createNoteList(10);

        // Perform GET request on MockMvc without request parameters and assert expectations.
        mockMvc.perform(get("/note/entries/recent"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithRequestParameter() throws Exception {

        // Create expected object.
        List<Note> expectedNotes = createNoteList(10);

        // Perform GET request on MockMvc with request parameters and assert expectations.
        mockMvc.perform(get("/note/entries/recent?count=10"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void testGetNote() throws Exception {

        // Create expected object.
        Note expectedNote = new Note(1L, new Date(), "Title", "Body");

        // Perform GET request on MockMvc with path variable and assert expectations.
        mockMvc.perform(get("/note/view?noteId=1"))
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", expectedNote));
    }

    @Test
    public void shouldShowNoteFormForAdding() throws Exception {

        // Perform GET request on MockMvc to add a note and assert expectations.
        mockMvc.perform(get("/note/add"))
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
        mockMvc.perform(get("/note/edit?noteId=1"))
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
        mockMvc.perform(post("/note/add")
                .param("title", "Title")
                .param("body", "Body"))
                .andExpect(redirectedUrl("/note/view?noteId=11&confirmation=added"));
    }

    @Test
    public void testEditNote() throws Exception {

        // Perform POST request to edit a note on MockMvc and assert expectations
        mockMvc.perform(post("/note/edit?noteId=1")
                .param("id", "1")
                .param("title", "New title")
                .param("body", "New body"))
                .andExpect(redirectedUrl("/note/view?noteId=1&confirmation=edited"));
    }

    private List<Note> createNoteList(int count) {
        List<Note> notes = new ArrayList<Note>();

        for (int i = 0; i < count; i++) {
            notes.add(new Note(new Date(), "Title", "Body"));
        }

        return notes;
    }
}
