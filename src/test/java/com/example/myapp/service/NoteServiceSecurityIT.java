package com.example.myapp.service;

import com.example.myapp.builder.dto.NoteDtoBuilder;
import com.example.myapp.builder.entity.NoteBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import com.example.myapp.repository.RoleRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
@WithMockUser(username = "mjones")
public class NoteServiceSecurityIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private NoteService noteService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @Transactional
    @Rollback
    public void findByIdShouldThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsername() {
        expectAccessDeniedException();
        noteService.findById(newNoteForUser("test").getId());
    }

    @Test
    @Transactional
    @Rollback
    public void findByIdShouldNotThrowExceptionWhenUsernameMatchesAuthenticatedUsername() {
        noteService.findById(newNoteForUser("mjones").getId());
    }

    @Test
    @Transactional
    @Rollback
    public void updateShouldThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsername() {
        expectAccessDeniedException();
        noteService.update(newNoteDtoForUser("test"));
    }

    @Test
    @Transactional
    @Rollback
    public void updateShouldNotThrowExceptionWhenUsernameMatchesAuthenticatedUsername() {
        noteService.update(newNoteDtoForUser("mjones"));
    }

    @Test
    @Transactional
    @Rollback
    public void deleteShouldThrowExceptionWhenUsernameDoesNotMatchAuthenticatedUsername() {
        expectAccessDeniedException();
        noteService.delete(newNoteForUser("test"));
    }

    @Test
    @Transactional
    @Rollback
    public void deleteShouldNotThrowExceptionWhenUsernameMatchesAuthenticatedUsername() {
        noteService.delete(newNoteForUser("mjones"));
    }

    private void expectAccessDeniedException() {
        expectedException.expect(AccessDeniedException.class);
        expectedException.expectMessage("Access is denied");
    }

    private Note newNoteForUser(String username) {
        User user = UserBuilder.givenUser().withFirstName("Mike")
                .withLastName("Jones")
                .withUsername(username)
                .withPassword("test123")
                .withRoles(Collections.singleton(roleRepository.findByType(RoleType.ROLE_USER)))
                .build();

        return NoteBuilder.givenNote(entityManager)
                .withUser(user)
                .withTitle("Title")
                .withBody("Body")
                .withCreatedAt(new Date())
                .build();
    }

    private NoteDto newNoteDtoForUser(String username) {
        Note note = newNoteForUser(username);

        return NoteDtoBuilder.givenNoteDto().withId(note.getId())
                .withUsername(username)
                .withTitle("New Title")
                .withBody("New body")
                .build();
    }
}
