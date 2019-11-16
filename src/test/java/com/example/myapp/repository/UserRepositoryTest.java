package com.example.myapp.repository;

import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private List<User> users;

    @Before
    public void setUp() {
        user1 = newUser("Darcee", "Byrom", "user1");

        users = Arrays.asList(
                user1,
                newUser("Darcee", "Byrom", "admin5"),
                newUser("Gustave", "Cornfield", "user5"),
                newUser("Goober", "Rehor", "admin1"),
                newUser("Avictor", "Bourdas", "user3"),
                newUser("Shelia", "Willson", "user4"),
                newUser("Zarla", "Bartlomiej", "admin3"),
                newUser("Boony", "Hertwell", "user2"),
                newUser("Katrina", "Brandle", "admin4"),
                newUser("Deane", "Caswill", "admin2"));
    }

    @Test
    @Transactional
    @Rollback
    public void findByUsernameShouldReturnExpectedResult() {
        User result = userRepository.findByUsername(user1.getUsername());
        Assert.assertEquals(user1, result);
    }

    @Test
    @Rollback
    @Transactional
    public void saveShouldSetExpectedFieldsForAdd() {
        final String firstName = "Mike";
        final String lastName = "Jones";
        final String username = "mjones";
        final String password = "password";
        final Set<Role> roles = Collections.singleton(findRole(RoleType.ROLE_USER));

        User user = UserBuilder.givenUser().withFirstName(firstName)
                .withLastName(lastName)
                .withUsername(username)
                .withPassword(password)
                .withRoles(roles)
                .build();

        userRepository.save(user);
        Assert.assertNotNull(user.getId());

        User savedUser = findUser(user.getId());

        Assert.assertNotNull(savedUser);
        Assert.assertEquals(firstName, savedUser.getFirstName());
        Assert.assertEquals(lastName, savedUser.getLastName());
        Assert.assertEquals(username, savedUser.getUsername());
        Assert.assertEquals(password, savedUser.getPassword());
        Assert.assertEquals(roles, savedUser.getRoles());
    }

    @Test
    @Rollback
    @Transactional
    public void saveShouldSetExpectedFieldsForUpdate() {
        final String firstName = "Mike";
        final String lastName = "Jones";
        final String username = "mjones";
        final String password = "test";
        final Role userRole = findRole(RoleType.ROLE_USER);
        final Role adminRole = findRole(RoleType.ROLE_ADMIN);

        Assert.assertNotEquals(firstName, user1.getFirstName());
        Assert.assertNotEquals(lastName, user1.getLastName());
        Assert.assertNotEquals(username, user1.getUsername());
        Assert.assertNotEquals(password, user1.getPassword());

        Assert.assertEquals(1, user1.getRoles().size());
        Assert.assertTrue(user1.getRoles().contains(userRole));
        Assert.assertFalse(user1.getRoles().contains(adminRole));

        user1.setFirstName(firstName);
        user1.setLastName(lastName);
        user1.setUsername(username);
        user1.setPassword(password);
        user1.getRoles().add(adminRole);

        userRepository.save(user1);

        User savedUser = findUser(user1.getId());

        Assert.assertNotNull(savedUser);
        Assert.assertEquals(firstName, savedUser.getFirstName());
        Assert.assertEquals(lastName, savedUser.getLastName());
        Assert.assertEquals(username, savedUser.getUsername());
        Assert.assertEquals(password, savedUser.getPassword());

        Assert.assertEquals(2, user1.getRoles().size());
        Assert.assertTrue(user1.getRoles().contains(userRole));
        Assert.assertTrue(user1.getRoles().contains(adminRole));

    }

    @Test
    @Transactional
    @Rollback
    public void deleteShouldDeleteExpectedUser() {
        Assert.assertTrue(userRepository.findById(user1.getId()).isPresent());

        userRepository.delete(user1);
        Assert.assertFalse(userRepository.findById(user1.getId()).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    public void findAllShouldReturnExpectedResultWhenSortFieldAndDirectionNotProvided() {
        final List<User> users = this.users.stream().sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());

        final int total = users.size();
        final int page = 1;
        final int size = 2;
        final Pageable pageable = PageRequest.of(page, size);

        List<User> expectedUsers = getPage(users, page, size);

        Page<User> result = userRepository.findAll(pageable);

        Assert.assertEquals(total, result.getTotalElements());
        Assert.assertEquals(getTotalPages(total, size), result.getTotalPages());
        Assert.assertEquals(expectedUsers, result.getContent());
        Assert.assertEquals(page, result.getNumber());
        Assert.assertEquals(size, result.getNumberOfElements());
        Assert.assertEquals(pageable, result.getPageable());
        Assert.assertEquals(size, result.getSize());
        Assert.assertEquals(pageable.getSort(), result.getSort());
    }

    @Test
    @Transactional
    @Rollback
    public void findAllShouldReturnExpectedResultWhenSortFieldAndDirectionAreProvided() {
        final List<User> users = this.users.stream().sorted(Comparator.comparing(User::getUsername).reversed())
                .collect(Collectors.toList());

        final int total = users.size();
        final int page = 1;
        final int size = 2;
        final Pageable pageable = PageRequest.of(page, size, Sort.by("username").descending());

        List<User> expectedUsers = getPage(users, page, size);

        Page<User> result = userRepository.findAll(pageable);

        Assert.assertEquals(total, result.getTotalElements());
        Assert.assertEquals(getTotalPages(total, size), result.getTotalPages());
        Assert.assertEquals(expectedUsers, result.getContent());
        Assert.assertEquals(page, result.getNumber());
        Assert.assertEquals(size, result.getNumberOfElements());
        Assert.assertEquals(pageable, result.getPageable());
        Assert.assertEquals(size, result.getSize());
        Assert.assertEquals(pageable.getSort(), result.getSort());
    }

    @Test
    @Transactional
    @Rollback
    public void searchShouldReturnExpectedResultWhenFirstNameMatches() {
        Page<User> result = userRepository.search(PageRequest.of(0, 1), user1.getFirstName());
        Assert.assertEquals(1, result.getContent().size());
        Assert.assertEquals(user1, result.getContent().get(0));
    }

    @Test
    @Transactional
    @Rollback
    public void searchShouldReturnExpectedResultWhenLastNameMatches() {
        Page<User> result = userRepository.search(PageRequest.of(0, 1), user1.getLastName());
        Assert.assertEquals(1, result.getContent().size());
        Assert.assertEquals(user1, result.getContent().get(0));
    }

    @Test
    @Transactional
    @Rollback
    public void searchShouldReturnExpectedResultWhenUsernameMatches() {
        Page<User> result = userRepository.search(PageRequest.of(0, 1), user1.getUsername());
        Assert.assertEquals(1, result.getContent().size());
        Assert.assertEquals(user1, result.getContent().get(0));
    }

    @Test
    @Transactional
    @Rollback
    public void searchShouldReturnExpectedResultWhenSortFieldAndDirectionNotProvided() {
        final String searchTerm = "admin";

        final List<User> users = this.users.stream().sorted(Comparator.comparingLong(User::getId))
                .filter(u -> u.getUsername().contains(searchTerm))
                .collect(Collectors.toList());

        final int total = users.size();
        final int page = 1;
        final int size = 2;
        final Pageable pageable = PageRequest.of(page, size);


        List<User> expectedUsers = getPage(users, page, size);

        Page<User> result = userRepository.search(pageable, searchTerm);

        Assert.assertEquals(total, result.getTotalElements());
        Assert.assertEquals(getTotalPages(total, size), result.getTotalPages());
        Assert.assertEquals(expectedUsers, result.getContent());
        Assert.assertEquals(page, result.getNumber());
        Assert.assertEquals(size, result.getNumberOfElements());
        Assert.assertEquals(pageable, result.getPageable());
        Assert.assertEquals(size, result.getSize());
        Assert.assertEquals(pageable.getSort(), result.getSort());
    }

    @Test
    @Transactional
    @Rollback
    public void searchShouldReturnExpectedResultWhenSortFieldAndDirectionAreProvided() {
        final String searchTerm = "admin";

        final List<User> users = this.users.stream().sorted(Comparator.comparing(User::getUsername).reversed())
                .filter(u -> u.getUsername().contains(searchTerm))
                .collect(Collectors.toList());

        final int total = users.size();
        final int page = 1;
        final int size = 2;
        final Pageable pageable = PageRequest.of(page, size, Sort.by("username").descending());


        List<User> expectedUsers = getPage(users, page, size);

        Page<User> result = userRepository.search(pageable, searchTerm);

        Assert.assertEquals(total, result.getTotalElements());
        Assert.assertEquals(getTotalPages(total, size), result.getTotalPages());
        Assert.assertEquals(expectedUsers, result.getContent());
        Assert.assertEquals(page, result.getNumber());
        Assert.assertEquals(size, result.getNumberOfElements());
        Assert.assertEquals(pageable, result.getPageable());
        Assert.assertEquals(size, result.getSize());
        Assert.assertEquals(pageable.getSort(), result.getSort());
    }

    private List<User> getPage(List<User> users, int page, int size) {
        int start = page * size;
        int end = start + size;

        return users.subList(start, end);
    }

    private int getTotalPages(int numberOfElements, int pageSize) {
        return (numberOfElements / pageSize) + (numberOfElements % pageSize);
    }

    private User newUser(String firstName, String lastName, String username) {
        Set<Role> roles = new HashSet<>();
        roles.add(findRole(RoleType.ROLE_USER));

        return UserBuilder.givenUser(entityManager).withFirstName(firstName)
                .withLastName(lastName)
                .withUsername(username)
                .withPassword("password")
                .withRoles(roles)
                .build();
    }

    private User findUser(long id) {
        return entityManager.find(User.class, id);
    }

    private Role findRole(RoleType type) {
        return (Role) entityManager.createQuery("SELECT r FROM Role r WHERE type = :type")
                .setParameter("type", type).getSingleResult();
    }
}
