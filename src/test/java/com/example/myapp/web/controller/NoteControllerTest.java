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

    //TODO ******************************************************** GET TESTS ********************************************************

    // TODO: refactor - get
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

    // TODO: refactor - get
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

    //TODO ******************************************************** ADD TESTS ********************************************************





    //TODO ******************************************************** UPDATE TESTS ********************************************************





    //TODO ******************************************************** DELETE TESTS ********************************************************

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
