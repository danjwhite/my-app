package com.example.myapp.web;

import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.service.INoteService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.nio.file.AccessDeniedException;
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
public class NoteControllerTest {

    @Autowired
    private NoteController noteController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private INoteService noteService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MockMvc mockMvc;

    @Before
    public void setup() {

        // Setup MockMvc to use NoteController.
        this.mockMvc = MockMvcBuilders.standaloneSetup(noteController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
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
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
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
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
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
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testGetNote() throws Exception {

        // Create expected object.
        Note expectedNote = noteService.findById(1L);

        // Perform GET request on MockMvc with path variable and assert expectations.
        mockMvc.perform(get("/note/1/view"))
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", expectedNote));
    }

    @Test(expected = NestedServletException.class)
    @WithMockUser(username = "drodman", password = "password123")
    public void testGetNoteAccessDenied() throws Exception {

        // Perform GET request on MockMvc with path variable and assert expectations.
        mockMvc.perform(get("/note/1/view"))
                .andExpect(redirectedUrl("/error/403"))
                .andExpect(status().is3xxRedirection());

        // Assert the expected cause of the thrown exception.
        expectedException.expectCause(isA(org.springframework.security.access.AccessDeniedException.class));
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testGetNoteInvalid() throws Exception {

        // Perform GET request on MockMvc with path variable and assert expectations.
        mockMvc.perform(get("/note/45/view"))
                .andExpect(redirectedUrl("/error/404"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void shouldShowNoteFormForAdding() throws Exception {

        // Perform GET request on MockMvc to add a note and assert expectations.
        mockMvc.perform(get("/note/add"))
                .andExpect(view().name("noteForm"))
                .andExpect(model().attributeExists("noteDto"))
                .andExpect(model().attribute("noteDto", hasProperty("noteId", is(nullValue()))))
                .andExpect(model().attribute("noteDto", hasProperty("username", is(nullValue()))))
                .andExpect(model().attribute("noteDto", hasProperty("title", is(nullValue()))))
                .andExpect(model().attribute("noteDto", hasProperty("body", is(nullValue()))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void shouldShowNoteFormForEditing() throws Exception {

        // Get the expected model attribute object.
        Note note = noteService.findById(1L);

        // Get the expected properties of the attribute object.
        String username = note.getUser().getUsername();
        String title = note.getTitle();
        String body = note.getBody();

        // Perform GET request on MockMvc to edit a note and assert expectations.
        mockMvc.perform(get("/note/1/edit"))
                .andExpect(view().name("noteForm"))
                .andExpect(model().attributeExists("noteDto"))
                .andExpect(model().attribute("noteDto", hasProperty("noteId", is(1L))))
                .andExpect(model().attribute("noteDto", hasProperty("username", is(username))))
                .andExpect(model().attribute("noteDto", hasProperty("title", is(title))))
                .andExpect(model().attribute("noteDto", hasProperty("body", is(body))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testAddNote() throws Exception {

        // Perform POST request to add a note on MockMvc and assert expectations.
        mockMvc.perform(post("/note/add")
                .param("title", "Title")
                .param("body", "Body"))
                .andExpect(redirectedUrl("/note/25/view?confirmation=added"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
    public void testEditNote() throws Exception {

        // Perform POST request to edit a note on MockMvc and assert expectations.
        mockMvc.perform(post("/note/1/edit")
                .param("id", "1")
                .param("username", "mjones")
                .param("title", "New title")
                .param("body", "New body"))
                .andExpect(redirectedUrl("/note/1/view?confirmation=edited"));
    }

    @Test
    @WithMockUser(username = "mjones", password = "password123", roles = {"USER", "ADMIN"})
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
