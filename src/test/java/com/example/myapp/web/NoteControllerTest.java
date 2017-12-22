package com.example.myapp.web;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceView;

import com.example.myapp.service.INoteService;
import com.example.myapp.domain.Note;

public class NoteControllerTest {

    @Test
    public void shouldShowAllNotes() throws Exception {

    }

    @Test
    public void shouldShowRecentNotesWithoutRequestParameter() throws Exception {

    }

    @Test
    public void shouldShowRecentNotesWithRequestParameter() throws Exception {

    }

    @Test
    public void testGetNote() throws Exception {

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
