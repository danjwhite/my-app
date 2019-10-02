package com.example.myapp.service;

import com.example.myapp.builder.dto.NoteDtoBuilder;
import com.example.myapp.builder.entity.NoteBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.dao.NoteRepository;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import org.easymock.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(EasyMockRunner.class)
public class NoteServiceTest extends EasyMockSupport {

    @Mock(type = MockType.STRICT)
    private NoteRepository noteRepositoryMock;

    @Mock(type = MockType.STRICT)
    private UserService userServiceMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private NoteService noteService;

    @Before
    public void setUp() {
        noteService = new NoteService(noteRepositoryMock, userServiceMock);
    }

    @Test
    public void findAllShouldReturnExpectedResult() {
        User user = UserBuilder.givenUser().withId(1L).build();
        List<Note> notes = Collections.singletonList(NoteBuilder.givenNote().withId(2L).build());

        expectGetLoggedInUser(user);
        expectFindAllByUserId(user.getId(), notes);
        replayAll();

        List<Note> result = noteService.findAll();
        verifyAll();

        Assert.assertEquals(notes, result);
    }

    @Test
    public void findRecentShouldReturnExpectedResult() {
        User user = UserBuilder.givenUser().withId(1L).build();
        List<Note> notes = Collections.singletonList(NoteBuilder.givenNote().withId(2L).build());

        expectGetLoggedInUser(user);
        expectFindTop10ByUserIdOrderByCreatedAtDesc(user.getId(), notes);
        replayAll();

        List<Note> result = noteService.findRecent();
        verifyAll();

        Assert.assertEquals(notes, result);
    }

    @Test
    public void findByIdShouldReturnExpectedResult() {
        long noteId = 1L;
        Note note = NoteBuilder.givenNote().withId(noteId).build();

        expectFindById(noteId, note);
        replayAll();

        Note result = noteService.findById(noteId);
        verifyAll();

        Assert.assertEquals(note, result);
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionWhenNoteNotFound() {
        long noteId = 1L;

        expectedException.expect(EntityNotFoundException.class);
        expectedException.expectMessage("Note not found for id: " + noteId);

        expectFindById(noteId, null);
        replayAll();

        noteService.findById(noteId);
        verifyAll();
    }

    @Test
    public void addShouldSetExpectedFields() {
        User user = UserBuilder.givenUser().withId(1L).build();
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withUsername("mjones").withTitle("Title").withBody("Body")
                .build();

        Capture<Note> noteCapture = EasyMock.newCapture();

        expectGetLoggedInUser(user);
        expectSave(noteCapture);
        replayAll();

        noteService.add(noteDto);
        verifyAll();

        Note note = noteCapture.getValue();
        Assert.assertNull(note.getId());
        Assert.assertNotNull(note.getCreatedAt());
        Assert.assertEquals(user, note.getUser());
        Assert.assertEquals(noteDto.getTitle(), note.getTitle());
        Assert.assertEquals(noteDto.getBody(), note.getBody());
    }

    @Test
    public void addShouldReturnExpectedResult() {
        User user = UserBuilder.givenUser().withId(1L).build();
        Note note = NoteBuilder.givenNote().withId(2L).build();

        expectGetLoggedInUser(user);
        expectSave(note);
        replayAll();

        Note result = noteService.add(new NoteDto());
        verifyAll();

        Assert.assertEquals(note, result);
    }

    @Test
    public void updateShouldSetExpectedFields() {
        Date createdAt = new Date();
        User user = UserBuilder.givenUser().withId(1L).build();

        Note note = NoteBuilder.givenNote()
                .withId(1L)
                .withCreatedAt(createdAt)
                .withUser(user)
                .withTitle("Title")
                .withBody("Body")
                .build();

        NoteDto noteDto = NoteDtoBuilder.givenNoteDto()
                .withNoteId(1L)
                .withUsername("mjones")
                .withTitle("New Title")
                .withBody("New body")
                .build();

        Capture<Note> noteCapture = EasyMock.newCapture();

        Assert.assertNotEquals(noteDto.getTitle(), note.getTitle());
        Assert.assertNotEquals(noteDto.getBody(), note.getBody());

        expectFindById(noteDto.getNoteId(), note);
        expectSave(noteCapture);
        replayAll();

        noteService.update(noteDto);
        verifyAll();

        Note updatedNote = noteCapture.getValue();
        Assert.assertEquals(noteDto.getNoteId(), updatedNote.getId());
        Assert.assertEquals(createdAt, updatedNote.getCreatedAt());
        Assert.assertEquals(user, updatedNote.getUser());
        Assert.assertEquals(noteDto.getTitle(), updatedNote.getTitle());
        Assert.assertEquals(noteDto.getBody(), updatedNote.getBody());
    }

    @Test
    public void updateShouldReturnExpectedResult() {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto().withNoteId(1L).build();
        Note note = NoteBuilder.givenNote().withId(1L).build();

        expectFindById(noteDto.getNoteId(), note);
        expectSave(note);
        replayAll();

        Note result = noteService.update(noteDto);
        verifyAll();

        Assert.assertEquals(note, result);
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenNoteNotFound() {
        NoteDto noteDto = NoteDtoBuilder.givenNoteDto().withNoteId(1L).build();

        expectedException.expect(EntityNotFoundException.class);
        expectedException.expectMessage("Note not found for id: " + noteDto.getNoteId());

        expectFindById(noteDto.getNoteId(), null);
        replayAll();

        noteService.update(noteDto);
        verifyAll();
    }

    @Test
    public void deleteShouldDeleteExpectedNote() {
        Note note = NoteBuilder.givenNote().withId(1L).build();

        expectDelete(note);
        replayAll();

        noteService.delete(note);
        verifyAll();
    }

    @Test
    public void countShouldReturnExpectedResult() {
        long count = 12L;
        User user = UserBuilder.givenUser().withId(1L).build();

        expectGetLoggedInUser(user);
        expectCountByUserId(user.getId(), count);
        replayAll();

        long result = noteService.count();
        verifyAll();

        Assert.assertEquals(count, result);
    }

    private void expectGetLoggedInUser(User user) {
        EasyMock.expect(userServiceMock.getLoggedInUser())
                .andReturn(user);
    }

    private void expectFindAllByUserId(long userId, List<Note> notes) {
        EasyMock.expect(noteRepositoryMock.findAllByUserId(userId))
                .andReturn(notes);
    }

    private void expectFindTop10ByUserIdOrderByCreatedAtDesc(long userId, List<Note> notes) {
        EasyMock.expect(noteRepositoryMock.findTop10ByUserIdOrderByCreatedAtDesc(userId))
                .andReturn(notes);
    }

    private void expectFindById(long id, Note note) {
        EasyMock.expect(noteRepositoryMock.findById(id))
                .andReturn(Optional.ofNullable(note));
    }

    @SuppressWarnings("ConstantConditions")
    private void expectSave(Capture<Note> noteCapture) {
        EasyMock.expect(noteRepositoryMock.save(EasyMock.capture(noteCapture)))
                .andReturn(new Note());
    }

    @SuppressWarnings("ConstantConditions")
    private void expectSave(Note note) {
        EasyMock.expect(noteRepositoryMock.save(EasyMock.anyObject(Note.class)))
                .andReturn(note);
    }

    private void expectDelete(Note note) {
        noteRepositoryMock.delete(note);
        EasyMock.expectLastCall();
    }

    private void expectCountByUserId(long userId, long count) {
        EasyMock.expect(noteRepositoryMock.countByUserId(userId))
                .andReturn(count);
    }
}
