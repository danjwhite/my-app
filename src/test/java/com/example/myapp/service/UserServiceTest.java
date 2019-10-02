package com.example.myapp.service;

import com.example.myapp.builder.dto.UserDtoBuilder;
import com.example.myapp.builder.dto.UserPasswordDtoBuilder;
import com.example.myapp.builder.dto.UserRegistrationDtoBuilder;
import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.dao.RoleRepository;
import com.example.myapp.dao.UserRepository;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;
import org.easymock.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@RunWith(EasyMockRunner.class)
public class UserServiceTest extends EasyMockSupport {

    @Mock(type = MockType.STRICT)
    private UserRepository userRepositoryMock;

    @Mock(type = MockType.STRICT)
    private RoleRepository roleRepositoryMock;

    @Mock(type = MockType.STRICT)
    private SecurityService securityServiceMock;

    @Mock(type = MockType.STRICT)
    private BCryptPasswordEncoder bCryptPasswordEncoderMock;

    @Mock(type = MockType.STRICT)
    private UserDetails userDetailsMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserService(userRepositoryMock, roleRepositoryMock, securityServiceMock, bCryptPasswordEncoderMock);
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionWhenUserNotFound() {
        long userId = 1L;

        expectedException.expect(EntityNotFoundException.class);
        expectedException.expectMessage("User not found for id: " + userId);

        expectFindUserById(userId, null);
        replayAll();

        userService.findById(userId);
        verifyAll();
    }

    @Test
    public void findByIdShouldReturnExpectedResult() {
        User user = UserBuilder.givenUser().withId(1L).build();

        expectFindUserById(user.getId(), user);
        replayAll();

        User result = userService.findById(user.getId());
        verifyAll();

        Assert.assertEquals(user, result);
    }

    @Test
    public void findByUsernameShouldReturnExpectedResult() {
        User user = UserBuilder.givenUser().withUsername("mjones").build();

        expectFindUserByUsername(user.getUsername(), user);
        replayAll();

        User result = userService.findByUsername(user.getUsername());
        verifyAll();

        Assert.assertEquals(user, result);
    }

    @Test
    public void userExistsShouldReturnExpectedResultWhenUserNotFound() {
        String username = "mjones";

        expectFindUserByUsername(username, null);
        replayAll();

        boolean result = userService.userExists(username);
        verifyAll();

        Assert.assertFalse(result);
    }

    @Test
    public void userExistsShouldReturnExpectedResultWhenUserFound() {
        User user = UserBuilder.givenUser().withUsername("mjones").build();

        expectFindUserByUsername(user.getUsername(), user);
        replayAll();

        boolean result = userService.userExists(user.getUsername());
        verifyAll();

        Assert.assertTrue(result);
    }

    @Test
    public void getLoggedInUserShouldReturnExpectedResult() {
        User user = UserBuilder.givenUser().withUsername("mjones").build();

        expectGetPrincipal();
        expectGetUsernameFromUserDetails(user.getUsername());
        expectFindUserByUsername(user.getUsername(), user);
        replayAll();

        User result = userService.getLoggedInUser();
        verifyAll();

        Assert.assertEquals(user, result);
    }

    @Test
    public void findAllShouldReturnExpectedResult() {
        List<User> users = Collections.singletonList(UserBuilder.givenUser().withId(1L).build());

        expectFindAllUsers(users);
        replayAll();

        List<User> result = userService.findAll();
        verifyAll();

        Assert.assertEquals(users, result);
    }

    @Test
    public void countShouldReturnExpectedResult() {
        long count = 12;

        expectCountUsers(count);
        replayAll();

        long result = userService.count();
        verifyAll();

        Assert.assertEquals(count, result);
    }

    @Test
    public void addShouldThrowEntityNotFoundExceptionWhenRoleNotFound() {
        Role role = RoleBuilder.givenRole().withId(1L).build();

        expectedException.expect(EntityNotFoundException.class);
        expectedException.expectMessage("Role not found for id: " + role.getId());

        UserRegistrationDto userRegistrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withRoles(Collections.singleton(role)).build();

        expectEncodePassword();
        expectFindRoleById(role.getId(), null);
        replayAll();

        userService.add(userRegistrationDto);
        verifyAll();
    }

    @Test
    public void addShouldSetExpectedFields() {
        String password = "test123";
        String encodedPassword = "98978hifd876f76njhj";

        Set<Role> roles = new HashSet<>(Arrays.asList(
                RoleBuilder.givenRole().withId(1L).build(),
                RoleBuilder.givenRole().withId(2L).build()));

        UserRegistrationDto userRegistrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword(password)
                .withRoles(roles)
                .build();

        Capture<User> userCapture = EasyMock.newCapture();

        expectEncodePassword(password, encodedPassword);
        expectFindEachRoleById(roles);
        expectSaveUser(userCapture);
        replayAll();

        userService.add(userRegistrationDto);
        verifyAll();

        User user = userCapture.getValue();
        Assert.assertEquals(userRegistrationDto.getFirstName(), user.getFirstName());
        Assert.assertEquals(userRegistrationDto.getLastName(), user.getLastName());
        Assert.assertEquals(userRegistrationDto.getUsername(), user.getUsername());
        Assert.assertEquals(encodedPassword, user.getPassword());
        Assert.assertEquals(userRegistrationDto.getRoles(), user.getRoles());
    }

    @Test
    public void addShouldReturnExpectedResult() {
        User user = UserBuilder.givenUser().withId(1L).build();

        expectEncodePassword();
        expectSaveUser(user);
        replayAll();

        User result = userService.add(new UserRegistrationDto());
        verifyAll();

        Assert.assertEquals(user, result);
    }

    @Test
    public void updateShouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage("Invalid username");

        UserDto userDto = UserDtoBuilder.givenUserDto().withUsername("mjones")
                .build();

        expectFindUserByUsername(userDto.getUsername(), null);
        replayAll();

        userService.update(userDto);
        verifyAll();
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenRoleNotFound() {
        Role role = RoleBuilder.givenRole().withId(1L).build();

        expectedException.expect(EntityNotFoundException.class);
        expectedException.expectMessage("Role not found for id: " + role.getId());

        UserDto userDto = UserDtoBuilder.givenUserDto().withUsername("mjones")
                .withRoles(Collections.singleton(role))
                .build();

        expectFindUserByUsername(userDto.getUsername(), new User());
        expectFindRoleById(role.getId(), null);
        replayAll();

        userService.update(userDto);
        verifyAll();
    }

    @Test
    public void updateShouldUpdateExpectedFields() {

        final Long id = 1L;
        final String username = "mjones";
        final String password = "test123";

        Role role1 = RoleBuilder.givenRole().withId(2L).build();
        Role role2 = RoleBuilder.givenRole().withId(3L).build();
        Role role3 = RoleBuilder.givenRole().withId(4L).build();
        Role role4 = RoleBuilder.givenRole().withId(5L).build();

        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);

        Set<Role> newRoles = new HashSet<>();
        newRoles.add(role1);
        newRoles.add(role3);
        newRoles.add(role4);

        UserDto userDto = UserDtoBuilder.givenUserDto().withFirstName("Mike")
                .withLastName("Jones")
                .withUsername(username)
                .withRoles(newRoles)
                .build();

        User user = UserBuilder.givenUser().withId(id).withFirstName("James")
                .withLastName("Smith")
                .withUsername(username)
                .withPassword(password)
                .withRoles(roles)
                .build();

        Assert.assertNotEquals(userDto.getFirstName(), user.getFirstName());
        Assert.assertNotEquals(userDto.getLastName(), user.getLastName());
        Assert.assertNotEquals(userDto.getRoles(), user.getRoles());

        Assert.assertEquals(id, user.getId());
        Assert.assertEquals(username, user.getUsername());
        Assert.assertEquals(password, user.getPassword());

        expectFindUserByUsername(userDto.getUsername(), user);
        expectFindRoleById(role3.getId(), role3);
        expectFindRoleById(role4.getId(), role4);
        replayAll();

        userService.update(userDto);
        verifyAll();

        Assert.assertEquals(id, user.getId());
        Assert.assertEquals(username, user.getUsername());
        Assert.assertEquals(password, user.getPassword());

        Assert.assertEquals(userDto.getFirstName(), user.getFirstName());
        Assert.assertEquals(userDto.getLastName(), user.getLastName());
        Assert.assertEquals(userDto.getRoles(), user.getRoles());
    }

    @Test
    public void updateShouldReturnExpectedResult() {
        UserDto userDto = UserDtoBuilder.givenUserDto().withUsername("mjones").build();
        User user = UserBuilder.givenUser().withId(1L).build();

        expectFindUserByUsername(userDto.getUsername(), user);
        replayAll();

        User result = userService.update(userDto);
        verifyAll();

        Assert.assertEquals(user, result);
    }

    @Test
    public void updatePasswordShouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage("Invalid username");

        UserPasswordDto userPasswordDto = UserPasswordDtoBuilder.givenUserPasswordDto().withUsername("mjones").build();

        expectFindUserByUsername(userPasswordDto.getUsername(), null);
        replayAll();

        userService.updatePassword(userPasswordDto);
        verifyAll();
    }

    @Test
    public void updatePasswordShouldUpdatePassword() {
        String newEncodedPassword = "newEncodedPassword";
        User user = UserBuilder.givenUser().withPassword("oldEncodedPassword").build();
        UserPasswordDto userPasswordDto = UserPasswordDtoBuilder.givenUserPasswordDto().withUsername("mjones")
                .withNewPassword("test456")
                .build();

        Assert.assertNotEquals(newEncodedPassword, user.getPassword());

        expectFindUserByUsername(userPasswordDto.getUsername(), user);
        expectEncodePassword(userPasswordDto.getNewPassword(), newEncodedPassword);
        replayAll();

        userService.updatePassword(userPasswordDto);
        verifyAll();

        Assert.assertEquals(newEncodedPassword, user.getPassword());
    }

    @Test
    public void updatePasswordShouldReturnExpectedResult() {

        UserPasswordDto userPasswordDto = UserPasswordDtoBuilder.givenUserPasswordDto().withUsername("mjones").build();
        User user = UserBuilder.givenUser().withId(1L).build();

        expectFindUserByUsername(userPasswordDto.getUsername(), user);
        expectEncodePassword();
        replayAll();

        User result = userService.updatePassword(userPasswordDto);
        verifyAll();

        Assert.assertEquals(user, result);
    }

    @Test
    public void deleteShouldDeleteExpectedUser() {
        User user = UserBuilder.givenUser().withId(1L).build();

        expectDeleteUser(user);
        replayAll();

        userService.delete(user);
        verifyAll();
    }

    private void expectSaveUser(Capture<User> userCapture) {
        EasyMock.expect(userRepositoryMock.save(EasyMock.capture(userCapture)))
                .andReturn(new User());
    }

    private void expectSaveUser(User user) {
        EasyMock.expect(userRepositoryMock.save(EasyMock.anyObject(User.class)))
                .andReturn(user);
    }

    private void expectCountUsers(long count) {
        EasyMock.expect(userRepositoryMock.count()).andReturn(count);
    }

    private void expectDeleteUser(User user) {
        userRepositoryMock.delete(user);
        EasyMock.expectLastCall();
    }

    private void expectFindUserById(long id, User user) {
        EasyMock.expect(userRepositoryMock.findById(id))
                .andReturn(Optional.ofNullable(user));
    }

    private void expectFindUserByUsername(String username, User user) {
        EasyMock.expect(userRepositoryMock.findByUsername(username))
                .andReturn(user);
    }

    private void expectFindAllUsers(List<User> users) {
        EasyMock.expect(userRepositoryMock.findAll()).andReturn(users);
    }

    private void expectFindRoleById(long id, Role role) {
        EasyMock.expect(roleRepositoryMock.findById(id))
                .andReturn(Optional.ofNullable(role));
    }

    private void expectFindEachRoleById(Set<Role> roles) {
        roles.forEach(role -> EasyMock.expect(roleRepositoryMock.findById(role.getId())).andReturn(Optional.of(role)));
    }

    private void expectGetPrincipal() {
        EasyMock.expect(securityServiceMock.getPrincipal()).andReturn(userDetailsMock);
    }

    private void expectGetUsernameFromUserDetails(String username) {
        EasyMock.expect(userDetailsMock.getUsername()).andReturn(username);
    }

    private void expectEncodePassword() {
        EasyMock.expect(bCryptPasswordEncoderMock.encode(EasyMock.anyString()))
                .andReturn("98978hifd876f76njhj");
    }

    private void expectEncodePassword(String password, String encodedPassword) {
        EasyMock.expect(bCryptPasswordEncoderMock.encode(password))
                .andReturn(encodedPassword);
    }
}
