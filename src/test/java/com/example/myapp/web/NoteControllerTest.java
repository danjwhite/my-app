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

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceView;

import com.example.myapp.data.NoteRepository;
import com.example.myapp.domain.Note;

public class NoteControllerTest {

    @Test
    public void shouldShowAllNotes() throws Exception {
        List<Note> expectedNotes = createNoteList(40);
        NoteRepository mockRepository = mock(NoteRepository.class);
        when(mockRepository.findNotes()).thenReturn(expectedNotes);

        NoteController controller = new NoteController(mockRepository);
        MockMvc mockMvc = standaloneSetup(controller)
                .setSingleView(new InternalResourceView("/WEB-INF/views/notes.jsp"))
                .build();

        mockMvc.perform(get("/note/entries/all"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithoutRequestParameter() throws Exception {
        List<Note> expectedNotes = createNoteList(20);
        NoteRepository mockRepository = mock(NoteRepository.class);
        when(mockRepository.findRecentNotes(20)).thenReturn(expectedNotes);

        NoteController controller = new NoteController(mockRepository);
        MockMvc mockMvc = standaloneSetup(controller)
                .setSingleView(new InternalResourceView("/WEB-INF/views/notes.jsp"))
                .build();

        mockMvc.perform(get("/note/entries/recent"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithRequestParameter() throws Exception {
        List<Note> expectedNotes = createNoteList(20);
        NoteRepository mockRepository = mock(NoteRepository.class);
        when(mockRepository.findRecentNotes(20)).thenReturn(expectedNotes);

        NoteController controller = new NoteController(mockRepository);
        MockMvc mockMvc = standaloneSetup(controller)
                .setSingleView(new InternalResourceView("/WEB-INF/views/notes.jsp"))
                .build();

        mockMvc.perform(get("/note/entries/recent?count=20"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void testGetNote() throws Exception {
        Note expectedNote = new Note(123L, "Title", new Date(), "Body");
        NoteRepository mockRepository = mock(NoteRepository.class);
        when(mockRepository.findOne(123L)).thenReturn(expectedNote);

        NoteController controller = new NoteController(mockRepository);
        MockMvc mockMvc = standaloneSetup(controller).build();

        mockMvc.perform(get("/note/123"))
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", expectedNote));
    }

    @Test
    public void testSaveNote() throws Exception {
        NoteRepository mockRepository = mock(NoteRepository.class);
        NoteController controller = new NoteController(mockRepository);
        MockMvc mockMvc = standaloneSetup(controller).build();

        mockMvc.perform(post("/note/add")
                .param("title", "Title")
                .param("body", "Body"))
                .andExpect(redirectedUrl("/note/0"));

        verify(mockRepository, atLeastOnce()).save(new Note("Title", new Date(), "Body"));
    }

    private List<Note> createNoteList(int count) {
        List<Note> notes = new ArrayList<Note>();

        for (int i = 0; i < count; i++) {
            notes.add(new Note("Title", new Date(), "Body"));
        }

        return notes;
    }
}
