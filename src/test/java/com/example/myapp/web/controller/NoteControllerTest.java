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
import com.example.myapp.test.TestUtil;
import com.example.myapp.test.WebMvcBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.collection.IsMapWithSize;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

    // ******************************************************** GET ALL TESTS ********************************************************

    @Test
    @WithMockUser(username = "mjones")
    public void getNotesShouldShowRecentNotesWhenDisplayParamIsNullAndDisplayCookieValueIsNull() throws Exception {
        final List<Note> notes = LongStream.range(2, 6).mapToObj(id -> newNote(id, loggedInUser)).collect(Collectors.toList());

        expectGetLoggedInUser();
        expectFindRecentNotes(notes);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes").session(mockHttpSession))
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

        mockMvc.perform(MockMvcRequestBuilders.get("/notes").session(mockHttpSession)
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

        mockMvc.perform(MockMvcRequestBuilders.get("/notes").session(mockHttpSession)
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

        mockMvc.perform(MockMvcRequestBuilders.get("/notes").session(mockHttpSession)
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

    // ******************************************************** GET TESTS ********************************************************
    @Test
    @WithMockUser(username = "mjones")
    public void getNoteShouldRedirectTo404NotFoundErrorPageWhenFindNoteByIdThrowsEntityNotFoundException() throws Exception {
        final long noteId = 2L;

        expectGetLoggedInUser();
        expectFindNoteByIdThrowsException(noteId, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/" + noteId))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNoteShouldRedirectTo404ForbiddenErrorPageWhenFindNoteByIdThrowsAccessDeniedException() throws Exception {
        final long noteId = 2L;

        expectGetLoggedInUser();
        expectFindNoteByIdThrowsException(noteId, new AccessDeniedException("Access id denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/" + noteId))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void getNoteShouldReturnExpectedResult() throws Exception {
        final Note note = newNote(1L, loggedInUser);
        final NoteDto noteDto = new NoteDto(note);

        expectGetLoggedInUser();
        expectFindNoteById(note.getId(), note);
        replayAll();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/notes/" + note.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        verifyAll();

        Assert.assertEquals(noteDto, TestUtil.jsonToObject(result.getResponse().getContentAsString(), NoteDto.class));
    }

    // ******************************************************** ADD TESTS ********************************************************
    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldReturnExpectedResultWhenTitleIsNull() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername(loggedInUser.getUsername())
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        saveNoteAndExpectFieldError(noteDto, "title", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldReturnExpectedResultWhenTitleIsEmpty() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername(loggedInUser.getUsername())
                .withTitle(StringUtils.EMPTY)
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        saveNoteAndExpectFieldError(noteDto, "title", "Cannot be blank");;
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldReturnExpectedResultWhenTitleIsBlank() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername(loggedInUser.getUsername())
                .withTitle(StringUtils.SPACE)
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        saveNoteAndExpectFieldError(noteDto, "title", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldReturnExpectedResultWhenTitleExceedsMaxLength() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername(loggedInUser.getUsername())
                .withTitle(RandomStringUtils.random(141))
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        saveNoteAndExpectFieldError(noteDto, "title", "Title must be within 140 characters.");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldReturnExpectedResultWhenBodyIsNull() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .build();

        expectGetLoggedInUser();
        saveNoteAndExpectFieldError(noteDto, "body", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldReturnExpectedResultWhenBodyIsEmpty() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody(StringUtils.EMPTY)
                .build();

        expectGetLoggedInUser();
        saveNoteAndExpectFieldError(noteDto, "body", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldReturnExpectedResultWhenBodyIsBlank() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody(StringUtils.SPACE)
                .build();

        expectGetLoggedInUser();
        saveNoteAndExpectFieldError(noteDto, "body", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldReturnExpectedResultWhenBodyExceedsMaxLength() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody(RandomStringUtils.random(5001))
                .build();

        expectGetLoggedInUser();
        saveNoteAndExpectFieldError(noteDto, "body", "Body must be within 5,000 characters.");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void saveNoteShouldReturnExpectedResultWhenNoErrorsPresent() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        expectAddNote(noteDto, new Note());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/notes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.objectToJson(noteDto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(TestUtil.emptyResponse());

        verifyAll();
    }

    // ******************************************************** UPDATE TESTS ********************************************************

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturnExpectedResultWhenTitleIsNull() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        updateNoteAndExpectFieldError(noteDto, "title", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturnExpectedResultWhenTitleIsEmpty() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle(StringUtils.EMPTY)
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        updateNoteAndExpectFieldError(noteDto, "title", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturnExpectedResultWhenTitleIsBlank() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle(StringUtils.SPACE)
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        updateNoteAndExpectFieldError(noteDto, "title", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturnExpectedResultWhenTitleExceedsMaxLength() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle(RandomStringUtils.random(141))
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        updateNoteAndExpectFieldError(noteDto, "title", "Title must be within 140 characters.");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturnExpectedResultWhenBodyIsNull() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .build();

        expectGetLoggedInUser();
        updateNoteAndExpectFieldError(noteDto, "body", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturnExpectedResultWhenBodyIsEmpty() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody(StringUtils.EMPTY)
                .build();

        expectGetLoggedInUser();
        updateNoteAndExpectFieldError(noteDto, "body", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturnExpectedResultWhenBodyIsBlank() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody(StringUtils.SPACE)
                .build();

        expectGetLoggedInUser();
        updateNoteAndExpectFieldError(noteDto, "body", "Cannot be blank");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturnExpectedResultWhenBodyExceedsMaxLength() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody(RandomStringUtils.random(5001))
                .build();

        expectGetLoggedInUser();
        updateNoteAndExpectFieldError(noteDto, "body", "Body must be within 5,000 characters.");
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldReturnExpectedResultWhenNoErrorsPresent() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        expectUpdateNote(noteDto);
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.put("/notes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.objectToJson(noteDto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(TestUtil.emptyResponse());

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldRedirectToExpectedViewWhenEntityNotFoundExceptionThrown() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        expectUpdateNoteThrowsException(noteDto, new EntityNotFoundException());
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.put("/notes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.objectToJson(noteDto)))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));
    }

    @Test
    @WithMockUser(username = "mjones")
    public void updateNoteShouldRedirectToExpectedViewWhenAccessDeniedExceptionThrown() throws Exception {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withId(1L)
                .withUsername(loggedInUser.getUsername())
                .withTitle("Title")
                .withBody("Body")
                .build();

        expectGetLoggedInUser();
        expectUpdateNoteThrowsException(noteDto, new AccessDeniedException("Access is denied"));
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.put("/notes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.objectToJson(noteDto)))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));
    }

    // ******************************************************** DELETE TESTS ********************************************************

    @Test
    @WithMockUser(username = "mjones")
    public void deleteNoteShouldReturnExpectedResult() throws Exception {
        final Note note = newNote(1L, loggedInUser);

        expectGetLoggedInUser();
        expectFindNoteById(note.getId(), note);
        expectDeleteNote(note);

        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.delete("/notes/" + note.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(TestUtil.emptyResponse());

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteNoteShouldRedirectToExpectedViewWhenEntityNotFoundExceptionThrown() throws Exception {
        final long noteId = 1L;

        expectGetLoggedInUser();
        expectFindNoteByIdThrowsException(noteId, new EntityNotFoundException());

        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.delete("/notes/" + noteId)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/404"));

        verifyAll();
    }

    @Test
    @WithMockUser(username = "mjones")
    public void deleteNoteShouldRedirectToExpectedViewWhenAccessDeniedExceptionThrown() throws Exception {
        final long noteId = 1L;

        expectGetLoggedInUser();
        expectFindNoteByIdThrowsException(noteId, new AccessDeniedException("Access is denied"));

        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.delete("/notes/" + noteId)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error/403"));

        verifyAll();
    }

    private void saveNoteAndExpectFieldError(NoteDto noteDto, String fieldName, String message) throws Exception {
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/notes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(noteDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapWithSize.aMapWithSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapContaining.hasEntry(fieldName, message)));

        verifyAll();
    }

    private void updateNoteAndExpectFieldError(NoteDto noteDto, String fieldName, String message) throws Exception {
        replayAll();

        mockMvc.perform(MockMvcRequestBuilders.put("/notes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.objectToJson(noteDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapWithSize.aMapWithSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", IsMapContaining.hasEntry(fieldName, message)));

        verifyAll();
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
