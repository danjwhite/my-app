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
    public void shouldShowRecentNotes() throws Exception {
        List<Note> expectedNotes = createNoteList(20);
        NoteRepository mockRepository = mock(NoteRepository.class);
        when(mockRepository.findRecentNotes()).thenReturn(expectedNotes);

        NoteController controller = new NoteController(mockRepository);
        MockMvc mockMvc = standaloneSetup(controller)
                .setSingleView(new InternalResourceView("/WEB-INF/views/posts.jsp"))
                .build();

        mockMvc.perform(get("/notes"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    private List<Note> createNoteList(int count) {
        List<Note> notes = new ArrayList<Note>();

        for (int i = 0; i < count; i++) {
            notes.add(new Note("Title", new Date(), "Body"));
        }

        return notes;
    }
}
