package com.example.myapp.web;

import com.example.myapp.domain.Note;
import com.example.myapp.service.INoteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
public class NoteControllerTest {

    @Autowired
    private NoteController noteController;

    @Autowired
    private INoteService noteService;

    private MockMvc mockMvc;

    @Before
    public void setup() {

        // Setup MockMvc to use NoteController.
        this.mockMvc = MockMvcBuilders.standaloneSetup(noteController).build();
    }

    @Test
    public void shouldShowAllNotes() throws Exception {

        // Create expected object.
        List<Note> expectedNotes = noteService.findAll();

        // Perform GET request on MockMvc and assert expectations.
        mockMvc.perform(get("/notes/view?display=all"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithoutRequestParameter() throws Exception {

        // Create expected object.
        List<Note> expectedNotes = noteService.findRecent();

        // Perform GET request on MockMvc without request parameters and assert expectations.
        mockMvc.perform(get("/notes/view"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void shouldShowRecentNotesWithRequestParameter() throws Exception {

        // Create expected object.
        List<Note> expectedNotes = noteService.findRecent();

        // Perform GET request on MockMvc with request parameters and assert expectations.
        mockMvc.perform(get("/notes/view?display=recent&maxResults=10"))
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", hasItems(expectedNotes.toArray())));
    }

    @Test
    public void testGetNote() throws Exception {

        // Create expected object.
        Note expectedNote = noteService.findById(1L);

        // Perform GET request on MockMvc with path variable and assert expectations.
        mockMvc.perform(get("/note/1/view"))
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", expectedNote));
    }

    @Test
    public void shouldShowNoteFormForAdding() throws Exception {

        // Perform GET request on MockMvc to add a note and assert expectations.
        mockMvc.perform(get("/note/add"))
                .andExpect(view().name("noteForm"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", hasProperty("id", is(nullValue()))))
                .andExpect(model().attribute("note", hasProperty("createdAt", is(nullValue()))))
                .andExpect(model().attribute("note", hasProperty("userId", is(nullValue()))))
                .andExpect(model().attribute("note", hasProperty("title", is(nullValue()))))
                .andExpect(model().attribute("note", hasProperty("body", is(nullValue()))))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldShowNoteFormForEditing() throws Exception {

        // Get the expected model attribute object.
        Note note = noteService.findById(1L);

        // Get the expected properties of the attribute object.
        Date createdAt = note.getCreatedAt();
        Long userId = note.getUserId();
        String title = note.getTitle();
        String body = note.getBody();

        // Perform GET request on MockMvc to edit a note and assert expectations.
        mockMvc.perform(get("/note/1/edit"))
                .andExpect(view().name("noteForm"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", hasProperty("id", is(1L))))
                .andExpect(model().attribute("note", hasProperty("createdAt", is(createdAt))))
                .andExpect(model().attribute("note", hasProperty("userId", is(userId))))
                .andExpect(model().attribute("note", hasProperty("title", is(title))))
                .andExpect(model().attribute("note", hasProperty("body", is(body))));
    }

    @Test
    public void testAddNote() throws Exception {

        // Perform POST request to add a note on MockMvc and assert expectations.
        mockMvc.perform(post("/note/add")
                .param("title", "Title")
                .param("body", "Body"))
                .andExpect(redirectedUrl("/note/25/view?confirmation=added"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testEditNote() throws Exception {

        // Perform POST request to edit a note on MockMvc and assert expectations.
        mockMvc.perform(post("/note/1/edit")
                .param("id", "1")
                .param("title", "New title")
                .param("body", "New body"))
                .andExpect(redirectedUrl("/note/1/view?confirmation=edited"));
    }

    @Test
    public void testDeleteNote() throws Exception {

        // Assert note count before delete.
        assertEquals(12, noteService.count());

        // Perform GET request to delete a note on MockMvc and assert expectations.
        mockMvc.perform(get("/note/1/delete"))
                .andExpect(redirectedUrl("/notes/view"));

        // Assert note count after delete.
        assertEquals(11, noteService.count());
    }
}
