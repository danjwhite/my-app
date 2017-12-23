package com.example.myapp.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.myapp.config.TestConfig;
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
        mockMvc.perform(get("/note/1"))
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", expectedNote));
    }

    @Test
    public void shouldShowNoteForm() throws Exception {

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/note/add"))
                .andExpect(view().name("noteForm"));
    }

    @Test
    public void testSaveNote() throws Exception {

        // Perform POST request on MockMvc and assert expectations.
        mockMvc.perform(post("/note/add")
                .param("title", "Title")
                .param("body", "Body"))
                .andExpect(redirectedUrl("/note/11"));
    }

    private List<Note> createNoteList(int count) {
        List<Note> notes = new ArrayList<Note>();

        for (int i = 0; i < count; i++) {
            notes.add(new Note(new Date(), "Title", "Body"));
        }

        return notes;
    }
}
