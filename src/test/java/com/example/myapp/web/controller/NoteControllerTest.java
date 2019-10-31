package com.example.myapp.web.controller;

import com.example.myapp.builder.dto.NoteDtoBuilder;
import com.example.myapp.builder.entity.NoteBuilder;
import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import com.example.myapp.service.NoteService;
import com.example.myapp.service.UserService;
import com.example.myapp.test.WebMvcBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@WebMvcTest(NoteController.class)
@ContextConfiguration(classes = {NoteControllerTest.NoteControllerTestConfig.class})
public class NoteControllerTest extends WebMvcBaseTest {

    private static final UserService userServiceMock = EasyMock.strictMock(UserService.class);
    private static final NoteService noteServiceMock = EasyMock.strictMock(NoteService.class);
    private static final UserDetails userDetailsMock = EasyMock.strictMock(UserDetails.class);

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession mockHttpSession;
    private User loggedInUser;

    @BeforeClass
    public static void init() {
        initMocks(userServiceMock, noteServiceMock, userDetailsMock);
    }

    @Before
    public void setUp() {
        mockHttpSession = new MockHttpSession();

        loggedInUser = UserBuilder.givenUser()
                .withId(1L)
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword(new BCryptPasswordEncoder().encode("test123"))
                .withRoles(Collections.singleton(newRole(1L, RoleType.ROLE_USER)))
                .build();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldShowRecentNotesWhenDisplayParamIsNullAndDisplayCookieValueIsNull() throws Exception {
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, loggedInUser)).collect(Collectors.toList());

        expectGetLoggedInUser();
        expectFindRecentNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/view").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notes"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "notes", "display"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("notes", notes))
                .andExpect(MockMvcResultMatchers.model().attribute("display", "recent"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldSetDisplayParamToCookieValueWhenDisplayParamIsNullAndCookieValueIsNotNull() throws Exception {
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, loggedInUser)).collect(Collectors.toList());

        expectGetLoggedInUser();
        expectFindAllNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/view").session(mockHttpSession)
                .cookie(new Cookie("displayCookie", "all")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notes"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "notes", "display"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("notes", notes))
                .andExpect(MockMvcResultMatchers.model().attribute("display", "all"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }


    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldAddDisplayCookieToResponseAndReturnExpectedViewWithExpectedAttributesWhenDisplayParamSetToAll() throws Exception {
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, loggedInUser)).collect(Collectors.toList());

        expectGetLoggedInUser();
        expectFindAllNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/view").session(mockHttpSession)
                .param("display", "all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notes"))
                .andExpect(MockMvcResultMatchers.cookie().exists("displayCookie"))
                .andExpect(MockMvcResultMatchers.cookie().value("displayCookie", "all"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "notes", "display"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("notes", notes))
                .andExpect(MockMvcResultMatchers.model().attribute("display", "all"));

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldAddDisplayCookieToResponseAndReturnExpectedViewWithExpectedAttributesWhenDisplayParamSetToRecent() throws Exception {
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, loggedInUser)).collect(Collectors.toList());

        expectGetLoggedInUser();
        expectFindRecentNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/view").session(mockHttpSession)
                .param("display", "recent"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notes"))
                .andExpect(MockMvcResultMatchers.cookie().exists("displayCookie"))
                .andExpect(MockMvcResultMatchers.cookie().value("displayCookie", "recent"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "notes", "display"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("notes", notes))
                .andExpect(MockMvcResultMatchers.model().attribute("display", "recent"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNoteShouldRedirectTo404NotFoundErrorPageWhenFindNoteByIdThrowsEntityNotFoundException() throws Exception {
        final long noteId = 2L;

        expectGetLoggedInUser();
        expectFindNoteByIdThrowsException(noteId, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/view"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNoteShouldRedirectTo404ForbiddenErrorPageWhenFindNoteByIdThrowsAccessDeniedException() throws Exception {
        final long noteId = 2L;

        expectGetLoggedInUser();
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
        final Note note = newNote(2L, loggedInUser);

        expectGetLoggedInUser();
        expectFindNoteById(note.getId(), note);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + note.getId() + "/view").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "note"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("note", note));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void addNoteShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/add").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto", "formType"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("noteDto", new NoteDto()))
                .andExpect(MockMvcResultMatchers.model().attribute("formType", "add"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteTitleIsNull() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add").session(mockHttpSession)
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }


    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteTitleIsEmpty() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add").session(mockHttpSession)
                .param("title", StringUtils.EMPTY)
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteTitleIsAllWhitespace() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add").session(mockHttpSession)
                .param("title", StringUtils.SPACE)
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteTitleExceedsMaxLength() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add").session(mockHttpSession)
                .param("title", RandomStringUtils.random(141, true, false))
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "Size"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteBodyIsNull() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add").session(mockHttpSession)
                .param("title", "Title")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteBodyIsEmpty() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add").session(mockHttpSession)
                .param("title", "Title")
                .param("body", StringUtils.EMPTY)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteBodyIsAllWhitespace() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add").session(mockHttpSession)
                .param("title", "Title")
                .param("body", StringUtils.SPACE)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldNotProceedWhenNoteBodyExceedsMaxLength() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add").session(mockHttpSession)
                .param("title", "Title")
                .param("body", RandomStringUtils.random(5001, true, false))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "Size"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldRedirectToExpectedViewWithExpectedAttributes() throws Exception {
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto().withTitle("Title").withBody("Body").build();
        final Note note = NoteBuilder.givenNote().withId(2L).build();

        expectGetLoggedInUser();
        expectAddNote(noteDto, note);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/add").session(mockHttpSession)
                .param("title", "Title")
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/note/" + note.getId() + "/view?confirmation=added"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editNoteShouldRedirectTo404NotFoundErrorPageWhenFindNoteByIdThrowsEntityNotFoundException() throws Exception {
        final long noteId = 2L;

        expectGetLoggedInUser();
        expectFindNoteByIdThrowsException(noteId, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/edit").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editNoteShouldRedirectTo403ForbiddenErrorPageWhenFindNoteByIdThrowsAccessDeniedException() throws Exception {
        final long noteId = 2L;

        expectGetLoggedInUser();
        expectFindNoteByIdThrowsException(noteId, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/edit").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void editNoteShouldReturnExpectedViewWithExpectedAttributes() throws Exception {
        final Note note = newNote(2L, loggedInUser);
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withNoteId(note.getId())
                .withUsername(note.getUser().getUsername())
                .withTitle(note.getTitle())
                .withBody(note.getBody())
                .build();

        expectGetLoggedInUser();
        expectFindNoteById(note.getId(), note);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + note.getId() + "/edit").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto", "formType"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                .andExpect(MockMvcResultMatchers.model().attribute("noteDto", noteDto))
                .andExpect(MockMvcResultMatchers.model().attribute("formType", "edit"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteTitleIsNull() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit").session(mockHttpSession)
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteTitleIsEmpty() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit").session(mockHttpSession)
                .param("title", StringUtils.EMPTY)
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteTitleIsAllWhitespace() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit").session(mockHttpSession)
                .param("title", StringUtils.SPACE)
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteTitleExceedsMaxLength() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit").session(mockHttpSession)
                .param("title", RandomStringUtils.random(141, true, false))
                .param("body", "Body")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "title", "Size"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteBodyIsNull() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit").session(mockHttpSession)
                .param("title", "Title")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteBodyIsEmpty() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit").session(mockHttpSession)
                .param("title", "Title")
                .param("body", StringUtils.EMPTY)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteBodyIsAllWhitespace() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit").session(mockHttpSession)
                .param("title", "Title")
                .param("body", StringUtils.SPACE)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "NotBlank"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldNotProceedWhenNoteBodyExceedsMaxLength() throws Exception {
        expectGetLoggedInUser();
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/2/edit").session(mockHttpSession)
                .param("title", "Title")
                .param("body", RandomStringUtils.random(5001, true, false))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("noteForm"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userInContext", "noteDto"))
                .andExpect(MockMvcResultMatchers.model().attribute("userInContext", loggedInUser))
                // TODO: Assert noteDto attribute - use flash attribute?
                .andExpect(MockMvcResultMatchers.model().errorCount(1))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("noteDto"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("noteDto", "body", "Size"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturn404NotFoundStatusWhenNoteNotFound() throws Exception {
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withNoteId(2L)
                .withUsername("mjones")
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        expectUpdateNoteThrowsException(noteDto, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/" + noteDto.getNoteId() + "/edit").session(mockHttpSession)
                .param("noteId", noteDto.getNoteId().toString())
                .param("username", noteDto.getUsername())
                .param("title", noteDto.getTitle())
                .param("body", noteDto.getBody())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown() throws Exception {
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withNoteId(2L)
                .withUsername("mjones")
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        expectUpdateNoteThrowsException(noteDto, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/" + noteDto.getNoteId() + "/edit").session(mockHttpSession)
                .param("noteId", noteDto.getNoteId().toString())
                .param("username", noteDto.getUsername())
                .param("title", noteDto.getTitle())
                .param("body", noteDto.getBody())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldRedirectToExpectedViewWithExpectedAttributes() throws Exception {
        final NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withNoteId(2L)
                .withUsername("mjones")
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        expectUpdateNote(noteDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/note/" + noteDto.getNoteId() + "/edit").session(mockHttpSession)
                .param("noteId", noteDto.getNoteId().toString())
                .param("username", noteDto.getUsername())
                .param("title", noteDto.getTitle())
                .param("body", noteDto.getBody())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/note/" + noteDto.getNoteId() + "/view?confirmation=edited"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteNoteShouldReturn404NotFoundStatusWhenNoteNotFound() throws Exception {
        final long noteId = 2L;

        expectGetLoggedInUser();
        expectFindNoteByIdThrowsException(noteId, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/delete").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }


    @Test
    @WithMockUser(username = "mjones")
    public void deleteNoteShouldReturn403ForbiddenStatusWhenAccessDeniedExceptionIsThrown() throws Exception {
        final long noteId = 2L;

        expectGetLoggedInUser();
        expectFindNoteByIdThrowsException(noteId, new AccessDeniedException("Access id denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + noteId + "/delete").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();

        Assert.assertNull(mockHttpSession.getAttribute("userInContext"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteNoteShouldRedirectToExpectedView() throws Exception {
        final Note note = newNote(2L, loggedInUser);

        expectGetLoggedInUser();
        expectFindNoteById(note.getId(), note);
        expectDeleteNote(note);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/note/" + note.getId() + "/delete").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/notes/view"));

        verifyAll();

        Assert.assertEquals(loggedInUser, mockHttpSession.getAttribute("userInContext"));
    }

    private void expectGetLoggedInUser() {
        EasyMock.expect(userServiceMock.getLoggedInUser())
                .andReturn(loggedInUser);
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

    private void expectDeleteNote(Note note) {
        noteServiceMock.delete(note);
        EasyMock.expectLastCall();
    }

    private Role newRole(long id, RoleType roleType) {
        return RoleBuilder.givenRole().withId(id).withType(roleType).build();
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
            return new NoteController(userServiceMock, noteServiceMock);
        }
    }
}
