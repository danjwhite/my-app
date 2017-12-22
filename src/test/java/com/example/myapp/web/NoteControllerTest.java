package com.example.myapp.web;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceView;

import com.example.myapp.service.INoteService;
import com.example.myapp.domain.Note;

public class NoteControllerTest {

    @Mock
    private INoteService noteService;

    @InjectMocks
    private NoteController noteController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(noteController).build();
    }

    @Test
    public void shouldShowAllNotes() throws Exception {

        // Create expected object and set up service to return it.
        List<Note> expectedNotes = createNoteList(40);
        when(noteService.findAll()).thenReturn(expectedNotes);

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/note/entries/all"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithoutRequestParameter() throws Exception {

        // Create expected object and set up service to return it.
        List<Note> expectedNotes = createNoteList(20);
        when(noteService.findRecent()).thenReturn(expectedNotes);

        // Perform GET request on MockMvc without request parameters and assert expectations.
        mockMvc.perform(get("/note/entries/recent"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithRequestParameter() throws Exception {

        // Create expected object and set up service to return it.
        List<Note> expectedNotes = createNoteList(20);
        when(noteService.findRecent(20)).thenReturn(expectedNotes);

        // Perform GET request on MockMvc with request parameters and assert expectations.
        mockMvc.perform(get("/note/entries/recent?count=20"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void testGetNote() throws Exception {
        // Create expected object and set up service to return it.
        Note expectedNote = new Note(123L, new Date(), "Title", "Body");
        when(noteService.findOne(123L)).thenReturn(expectedNote);

        mockMvc.perform(get("/note/123"))
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", expectedNote));
    }

    @Test
    public void shouldShowNoteForm() throws Exception {

    }

    @Test
    public void testSaveNote() throws Exception {

    }

    private List<Note> createNoteList(int count) {
        List<Note> notes = new ArrayList<Note>();

        for (int i = 0; i < count; i++) {
            notes.add(new Note(new Date(), "Title", "Body"));
        }

        return notes;
    }
}
