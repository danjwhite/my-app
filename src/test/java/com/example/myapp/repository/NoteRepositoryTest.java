package com.example.myapp.repository;

import com.example.myapp.builder.entity.NoteBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Note;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class NoteRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional
    @Rollback
    public void countShouldReturnExpectedResult() {
        User user1 = newUser("user1");
        User user2 = newUser("user2");

        IntStream.range(0, 2).forEach(i -> newNote(user1));
        IntStream.range(0, 2).forEach(i -> newNote(user2));

        Assert.assertEquals(4, noteRepository.count());
    }

    @Test
    @Transactional
    @Rollback
    public void findAllByUserIdShouldReturnExpectedResult() {
        User user1 = newUser("user1");
        User user2 = newUser("user2");

        IntStream.range(0, 2).forEach(i -> newNote(user1));
        IntStream.range(0, 4).forEach(i -> newNote(user2));

        Assert.assertEquals(2, noteRepository.countByUserId(user1.getId()));
    }

    @Test
    @Transactional
    @Rollback
    public void findTop5ByUserIdOrderByCreatedAtDescShouldReturnFiveMostRecentNotes() {
        User user1 = newUser("user1");
        User user2 = newUser("user2");

        IntStream.range(0, 11).forEach(i -> newNote(user1));
        IntStream.range(0, 4).forEach(i -> newNote(user2));

        List<Note> notes = noteRepository.findTop5ByUserIdOrderByCreatedAtDesc(user1.getId());
        Assert.assertEquals(5, notes.size());

        Date lastDate = null;
        for (Note note : notes) {
            Assert.assertEquals(user1, note.getUser());
            if (lastDate != null) {
                Assert.assertTrue(note.getCreatedAt().before(lastDate));
            }

            lastDate = note.getCreatedAt();
        }
    }

    @Test
    @Transactional
    @Rollback
    public void findByIdShouldReturnExpectedResult() {
        Note note = newNote(newUser("mjones"));

        Optional<Note> result = noteRepository.findById(note.getId());
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(note, result.get());
    }

    @Test
    @Transactional
    @Rollback
    public void saveShouldSetExpectedValuesForAdd() {
        final User user = newUser("mjones");
        final String title = "Title";
        final String body = "Body";
        final Date createdAt = new Date();

        Note note = NoteBuilder.givenNote().withUser(user)
                .withTitle(title)
                .withBody(body)
                .withCreatedAt(createdAt)
                .build();

        noteRepository.save(note);
        Assert.assertNotNull(note.getId());

        Optional<Note> savedNote = noteRepository.findById(note.getId());
        Assert.assertTrue(savedNote.isPresent());
        Assert.assertEquals(note, savedNote.get());

        Assert.assertEquals(user, savedNote.get().getUser());
        Assert.assertEquals(title, savedNote.get().getTitle());
        Assert.assertEquals(body, savedNote.get().getBody());
        Assert.assertEquals(createdAt, savedNote.get().getCreatedAt());
    }

    @Test
    @Transactional
    @Rollback
    public void deleteShouldDeleteExpectedNote() {
        Note note = newNote(newUser("mjones"));
        Assert.assertTrue(noteRepository.findById(note.getId()).isPresent());

        noteRepository.delete(note);
        Assert.assertFalse(noteRepository.findById(note.getId()).isPresent());
    }

    private Note newNote(User user) {
        return NoteBuilder.givenNote(entityManager).withUser(user)
                .withTitle("Title")
                .withBody("Body")
                .withCreatedAt(randomDate())
                .build();
    }

    private User newUser(String username) {
        return UserBuilder.givenUser(entityManager).withFirstName("Test").withLastName("User")
                .withUsername(username)
                .withPassword("test123")
                .withRoles(Collections.singleton(roleRepository.findByType(RoleType.ROLE_USER)))
                .build();
    }

    private Date randomDate() {
        Date start = new GregorianCalendar(2018, Calendar.JANUARY, 1).getTime();
        Date end = new GregorianCalendar(2018, Calendar.DECEMBER, 31).getTime();

        return new Date(ThreadLocalRandom.current().nextLong(start.getTime(), end.getTime()));
    }
}
