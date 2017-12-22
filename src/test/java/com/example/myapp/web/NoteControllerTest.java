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

import com.example.myapp.dao.NoteRepository;
import com.example.myapp.domain.Note;

public class NoteControllerTest {

    @Test
    public void shouldShowAllNotes() throws Exception {

        // Create expected object.
        List<Note> expectedNotes = createNoteList(40);

        // Create mock repository that will return expected object.
        NoteRepository mockRepository = mock(NoteRepository.class);
        when(mockRepository.findNotes()).thenReturn(expectedNotes);

        // Create controller with injected repository.
        NoteController controller = new NoteController(mockRepository);

        // Set up MockMvc to use controller and view.
        MockMvc mockMvc = standaloneSetup(controller)
                .setSingleView(new InternalResourceView("/WEB-INF/views/notes.jsp"))
                .build();

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/note/entries/all"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithoutRequestParameter() throws Exception {
        // Create expected object.
        List<Note> expectedNotes = createNoteList(20);

        // Create mock repository that will return expected object.
        NoteRepository mockRepository = mock(NoteRepository.class);
        when(mockRepository.findRecentNotes(20)).thenReturn(expectedNotes);

        // Create controller with injected repository.
        NoteController controller = new NoteController(mockRepository);

        // Set up MockMvc to use controller and view.
        MockMvc mockMvc = standaloneSetup(controller)
                .setSingleView(new InternalResourceView("/WEB-INF/views/notes.jsp"))
                .build();

        // Perform GET request on MockMvc without request parameters and assert expectations.
        mockMvc.perform(get("/note/entries/recent"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithRequestParameter() throws Exception {
        // Create expected object.
        List<Note> expectedNotes = createNoteList(20);

        // Create mock repository that will return expected object.
        NoteRepository mockRepository = mock(NoteRepository.class);
        when(mockRepository.findRecentNotes(20)).thenReturn(expectedNotes);

        // Create controller with injected repository.
        NoteController controller = new NoteController(mockRepository);

        // Set up MockMvc to use controller and view.
        MockMvc mockMvc = standaloneSetup(controller)
                .setSingleView(new InternalResourceView("/WEB-INF/views/notes.jsp"))
                .build();

        // Perform GET request on MockMvc with request parameters and assert expectations.
        mockMvc.perform(get("/note/entries/recent?count=20"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void testGetNote() throws Exception {
        // Create expected object.
        Note expectedNote = new Note(123L, new Date(), "Title", "Body");

        // Create mock repository that will return expected object.
        NoteRepository mockRepository = mock(NoteRepository.class);
        when(mockRepository.findOne(123L)).thenReturn(expectedNote);

        // Create controller with injected repository.
        NoteController controller = new NoteController(mockRepository);

        // Set up MockMvc to use controller and view.
        MockMvc mockMvc = standaloneSetup(controller).build();

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/note/123"))
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", expectedNote));
    }

    @Test
    public void shouldShowNoteForm() throws Exception {
        // Create controller.
        NoteController controller = new NoteController();

        // Set up MockMvc to use controller.
        MockMvc mockMvc = standaloneSetup(controller).build();

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/note/add"))
                .andExpect(view().name("noteForm"));
    }

    @Test
    public void testSaveNote() throws Exception {

        // Create mock repository.
        NoteRepository mockRepository = mock(NoteRepository.class);

        // Create controller with injected repository.
        NoteController controller = new NoteController(mockRepository);

        // Set up MockMvc to use controller.
        MockMvc mockMvc = standaloneSetup(controller).build();

        // Perform POST request on MockMvc and assert expectations.
        mockMvc.perform(post("/note/add")
                .param("title", "Title")
                .param("body", "Body"))
                .andExpect(redirectedUrl("/note/0"));

        // Verify that the mock repository was actually used to save the form dao.
        verify(mockRepository, atLeastOnce()).save(new Note(new Date(), "Title", "Body"));
    }

    private List<Note> createNoteList(int count) {
        List<Note> notes = new ArrayList<Note>();

        for (int i = 0; i < count; i++) {
            notes.add(new Note(new Date(), "Title", "Body"));
        }

        return notes;
    }
}
