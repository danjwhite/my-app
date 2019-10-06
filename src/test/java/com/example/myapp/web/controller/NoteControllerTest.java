package com.example.myapp.web.controller;

import com.example.myapp.builder.dto.NoteDtoBuilder;
import com.example.myapp.builder.entity.NoteBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import com.example.myapp.service.NoteService;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import com.example.myapp.test.WebMvcBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@WebMvcTest(NoteController.class)
@ContextConfiguration(classes = {NoteControllerTest.NoteControllerTestConfig.class})
public class NoteControllerTest extends WebMvcBaseTest {

    private static final SecurityService securityServiceMock = EasyMock.strictMock(SecurityService.class);
    private static final UserService userServiceMock = EasyMock.strictMock(UserService.class);
    private static final NoteService noteServiceMock = EasyMock.strictMock(NoteService.class);
    private static final UserDetails userDetailsMock = EasyMock.strictMock(UserDetails.class);

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void init() {
        initMocks(securityServiceMock, userServiceMock, noteServiceMock, userDetailsMock);
    }

    @Before
    public void setUp() {
        resetAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldShowRecentNotesWhenDisplayParamIsNullAndDisplayCookieValueIsNull() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, user)).collect(Collectors.toList());

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindRecentNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/view"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notes"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "notes", "display"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andExpect(MockMvcResultMatchers.model().attribute("notes", notes))
                .andExpect(MockMvcResultMatchers.model().attribute("display", "recent"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldSetDisplayParamToCookieValueWhenDisplayParamIsNullAndCookieValueIsNotNull() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, user)).collect(Collectors.toList());

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindAllNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/view")
                .cookie(new Cookie("displayCookie", "all")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notes"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "notes", "display"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andExpect(MockMvcResultMatchers.model().attribute("notes", notes))
                .andExpect(MockMvcResultMatchers.model().attribute("display", "all"));

        verifyAll();
    }


    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldAddDisplayCookieToResponseAndReturnExpectedViewWithExpectedAttributesWhenDisplayParamSetToAll() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, user)).collect(Collectors.toList());

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindAllNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/view")
                .param("display", "all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notes"))
                .andExpect(MockMvcResultMatchers.cookie().exists("displayCookie"))
                .andExpect(MockMvcResultMatchers.cookie().value("displayCookie", "all"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "notes", "display"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andExpect(MockMvcResultMatchers.model().attribute("notes", notes))
                .andExpect(MockMvcResultMatchers.model().attribute("display", "all"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldAddDisplayCookieToResponseAndReturnExpectedViewWithExpectedAttributesWhenDisplayParamSetToRecent() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, user)).collect(Collectors.toList());

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindRecentNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/view")
                .param("display", "recent"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notes"))
                .andExpect(MockMvcResultMatchers.cookie().exists("displayCookie"))
                .andExpect(MockMvcResultMatchers.cookie().value("displayCookie", "recent"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "notes", "display"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andExpect(MockMvcResultMatchers.model().attribute("notes", notes))
                .andExpect(MockMvcResultMatchers.model().attribute("display", "recent"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldShowAllNotesWhenDisplayParamIsNotSetToRecent() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, user)).collect(Collectors.toList());

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindAllNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/view")
                .param("display", "all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notes"))
                .andExpect(MockMvcResultMatchers.cookie().exists("displayCookie"))
                .andExpect(MockMvcResultMatchers.cookie().value("displayCookie", "all"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "notes", "display"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andExpect(MockMvcResultMatchers.model().attribute("notes", notes))
                .andExpect(MockMvcResultMatchers.model().attribute("display", "all"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void getNoteShouldReturn404NotFoundStatusWhenNoteNotFound() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final long noteId = 2L;

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindNoteByIdThrowsException(noteId, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/view"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void getNoteShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final long noteId = 2L;

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindNoteByIdThrowsException(noteId, new AccessDeniedException("Access id denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/view"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNoteShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final Note note = newNote(2L, user);

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindNoteById(note.getId(), note);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + note.getId() + "/view"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "note"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andExpect(MockMvcResultMatchers.model().attribute("note", note));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addNoteShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final String username = "mjones";
        final User user = newUser();

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/add"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "noteDto", "formType"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andExpect(MockMvcResultMatchers.model().attribute("noteDto", new NoteDto()))
                .andExpect(MockMvcResultMatchers.model().attribute("formType", "add"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteTitleIsNull() throws Exception {
        final String username = "mjones";
        final User user = newUser();

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add")
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotEmpty"));

        verifyAll();
    }


    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteTitleIsBlank() throws Exception {
        final String username = "mjones";
        final User user = newUser();

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add")
                .param("title", StringUtils.EMPTY)
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotEmpty"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteTitleExceedsMaxLength() throws Exception {
        final String username = "mjones";
        final User user = newUser();

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add")
                .param("title", RandomStringUtils.random(141, true, false))
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "Size"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteBodyIsNull() throws Exception {
        final String username = "mjones";
        final User user = newUser();

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add")
                .param("title", "Title")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotEmpty"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteBodyIsBlank() throws Exception {
        final String username = "mjones";
        final User user = newUser();

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add")
                .param("title", "Title")
                .param("body", StringUtils.EMPTY)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotEmpty"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteBodyExceedsMaxLength() throws Exception {
        final String username = "mjones";
        final User user = newUser();

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add")
                .param("title", "Title")
                .param("body", RandomStringUtils.random(5001, true, false))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "Size"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldRedirectToExpectedViewWithExpectedAttributes() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto().withTitle("Title").withBody("Body").build();
        final Note note = NoteBuilder.givenNote().withId(2L).build();

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectAddNote(noteDto, note);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add")
                .param("title", "Title")
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/note/" + note.getId() + "/view?confirmation=added"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void editNoteShouldReturn404NotFoundStatusWhenNoteNotFound() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final long noteId = 2L;

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindNoteByIdThrowsException(noteId, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/edit"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    // TODO: Rename
    @Test
    @WithMockUser(username = "mjones")
    public void editNoteShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final long noteId = 2L;

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindNoteByIdThrowsException(noteId, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/edit"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editNoteShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final String username = "mjones";
        final User user = newUser();
        final Note note = newNote(2L, user);
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withNoteId(note.getId())
                .withUsername(note.getUser().getUsername())
                .withTitle(note.getTitle())
                .withBody(note.getBody())
                .build();

        expectGetAuthenticationPrincipal(username);
        expectFindUserByUsername(username, user);
        expectFindNoteById(note.getId(), note);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + note.getId() + "/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "noteDto", "formType"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andExpect(MockMvcResultMatchers.model().attribute("noteDto", noteDto))
                .andExpect(MockMvcResultMatchers.model().attribute("formType", "edit"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteTitleIsNull() throws Exception {
        final User user = newUser();

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit")
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotEmpty"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteTitleIsBlank() throws Exception {
        final User user = newUser();

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit")
                .param("title", StringUtils.EMPTY)
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotEmpty"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteTitleExceedsMaxLength() throws Exception {
        final User user = newUser();

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit")
                .param("title", RandomStringUtils.random(141, true, false))
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "Size"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteBodyIsNull() throws Exception {
        final User user = newUser();

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit")
                .param("title", "Title")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotEmpty"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteBodyIsBlank() throws Exception {
        final User user = newUser();

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit")
                .param("title", "Title")
                .param("body", StringUtils.EMPTY)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotEmpty"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteBodyExceedsMaxLength() throws Exception {
        final User user = newUser();

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit")
                .param("title", "Title")
                .param("body", RandomStringUtils.random(5001, true, false))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "Size"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturn404NotFoundStatusWhenNoteNotFound() throws Exception {
        final User user = newUser();
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withNoteId(2L)
                .withUsername("mjones")
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        expectUpdateNoteThrowsException(noteDto, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/" + noteDto.getNoteId() + "/edit")
                .param("noteId", noteDto.getNoteId().toString())
                .param("username", noteDto.getUsername())
                .param("title", noteDto.getTitle())
                .param("body", noteDto.getBody())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown() throws Exception {
        final User user = newUser();
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withNoteId(2L)
                .withUsername("mjones")
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        expectUpdateNoteThrowsException(noteDto, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/" + noteDto.getNoteId() + "/edit")
                .param("noteId", noteDto.getNoteId().toString())
                .param("username", noteDto.getUsername())
                .param("title", noteDto.getTitle())
                .param("body", noteDto.getBody())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldRedirectToExpectedViewWithExpectedAttributes() throws Exception {
        final User user = newUser();
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withNoteId(2L)
                .withUsername("mjones")
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        expectUpdateNote(noteDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/" + noteDto.getNoteId() + "/edit")
                .param("noteId", noteDto.getNoteId().toString())
                .param("username", noteDto.getUsername())
                .param("title", noteDto.getTitle())
                .param("body", noteDto.getBody())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/note/" + noteDto.getNoteId() + "/view?confirmation=edited"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteNoteShouldReturn404NotFoundStatusWhenNoteNotFound() throws Exception {
        final User user = newUser();
        final long noteId = 2L;

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        expectFindNoteByIdThrowsException(noteId, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/delete"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));
    }


    @Test
    @WithMockUser(username = "mjones")
    public void deleteNoteShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown() throws Exception {
        final User user = newUser();
        final long noteId = 2L;

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        expectFindNoteByIdThrowsException(noteId, new AccessDeniedException("Access id denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/delete"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteNoteShouldRedirectToExpectedView() throws Exception {
        final User user = newUser();
        final Note note = newNote(2L, user);

        expectGetAuthenticationPrincipal(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        expectFindNoteById(note.getId(), note);
        expectDeleteNote(note);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + note.getId() + "/delete"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/notes/view"));
    }

    private void expectGetAuthenticationPrincipal(String username) {
        EasyMock.expect(securityServiceMock.getPrincipal())
                .andReturn(userDetailsMock);
        EasyMock.expect(userDetailsMock.getUsername())
                .andReturn(username);
    }

    private void expectFindUserByUsername(String username, User user) {
        EasyMock.expect(userServiceMock.findByUsername(username))
                .andReturn(user);
    }

    private void expectFindRecentNotes(List<Note> notes) {
        EasyMock.expect(noteServiceMock.findRecent()).andReturn(notes);
    }

    private void expectFindAllNotes(List<Note> notes) {
        EasyMock.expect(noteServiceMock.findAll()).andReturn(notes);
    }

    private <T extends RuntimeException> void expectFindNoteByIdThrowsException(long noteId, T exception) {
        EasyMock.expect(noteServiceMock.findById(noteId)).andThrow(exception);
    }

    private void expectFindNoteById(long noteId, Note note) {
        EasyMock.expect(noteServiceMock.findById(noteId)).andReturn(note);
    }

    private void expectAddNote(NoteDto noteDto, Note note) {
        EasyMock.expect(noteServiceMock.add(noteDto)).andReturn(note);
    }

    private <T extends RuntimeException> void expectUpdateNoteThrowsException(NoteDto noteDto, T exception) {
        EasyMock.expect(noteServiceMock.update(noteDto)).andThrow(exception);
    }

    private void expectUpdateNote(NoteDto noteDto) {
        EasyMock.expect(noteServiceMock.update(noteDto)).andReturn(new Note());
    }

    private void expectDeleteNoteThrowsAccessDeniedException(Note note) {
        noteServiceMock.delete(note);
        EasyMock.expectLastCall().andThrow(new AccessDeniedException("Access is denied"));
    }

    private void expectDeleteNote(Note note) {
        noteServiceMock.delete(note);
        EasyMock.expectLastCall();
    }

    private User newUser() {
        return UserBuilder.givenUser().withId(1L)
                .withUsername("mjones")
                .build();
    }

    private Note newNote(long id, User user) {
        return NoteBuilder.givenNote().withId(id).withUser(user)
                .withTitle("Title")
                .withBody("Body")
                .withCreatedAt(new Date())
                .build();
    }

    @Configuration
    @Import(WebMvcBaseTest.TestConfig.class)
    static class NoteControllerTestConfig {

        @Bean
        public NoteController noteController() {
            return new NoteController(securityServiceMock, userServiceMock, noteServiceMock);
        }
    }
}
