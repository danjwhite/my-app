package com.example.myapp.service;

import com.example.myapp.builder.dto.UserDtoBuilder;
import com.example.myapp.builder.dto.UserPasswordDtoBuilder;
import com.example.myapp.builder.dto.UserRegistrationDtoBuilder;
import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.builder.entity.UserBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.repository.RoleRepository;
import com.example.myapp.repository.UserRepository;
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
import java.util.stream.Collectors;

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
    public void findByUsernameShouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage("Invalid username.");

        String username = "mjones";
        expectFindUserByUsername(username, null);
        replayAll();

        userService.findByUsername(username);
        verifyAll();
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
    public void getLoggedInUserShouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage("Invalid username.");

        String username = "mjones";

        expectGetPrincipal();
        expectGetUsernameFromUserDetails(username);
        expectFindUserByUsername(username, null);
        replayAll();

        userService.getLoggedInUser();
        verifyAll();
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
    public void addShouldSetExpectedFields() {
        String password = "test123";
        String encodedPassword = "98978hifd876f76njhj";

        List<Role> roles = Arrays.asList(
                RoleBuilder.givenRole().withType(RoleType.ROLE_USER).build(),
                RoleBuilder.givenRole().withType(RoleType.ROLE_ADMIN).build());

        List<RoleType> roleTypes = roles.stream().map(Role::getType).collect(Collectors.toList());

        UserRegistrationDto userRegistrationDto = UserRegistrationDtoBuilder.givenUserRegistrationDto()
                .withFirstName("Mike")
                .withLastName("Jones")
                .withUsername("mjones")
                .withPassword(password)
                .withRoleTypes(roleTypes)
                .build();

        Capture<User> userCapture = EasyMock.newCapture();

        expectEncodePassword(password, encodedPassword);
        expectFindRolesByTypeList(roleTypes, roles);
        expectSaveUser(userCapture);
        replayAll();

        userService.add(userRegistrationDto);
        verifyAll();

        User user = userCapture.getValue();
        Assert.assertEquals(userRegistrationDto.getFirstName(), user.getFirstName());
        Assert.assertEquals(userRegistrationDto.getLastName(), user.getLastName());
        Assert.assertEquals(userRegistrationDto.getUsername(), user.getUsername());
        Assert.assertEquals(encodedPassword, user.getPassword());
        Assert.assertEquals(userRegistrationDto.getRoleTypes(), roleTypes);
    }

    @Test
    public void addShouldReturnExpectedResult() {
        User user = UserBuilder.givenUser().withId(1L).build();

        expectEncodePassword();
        expectFindRolesByTypeList();
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
    public void updateShouldUpdateExpectedFields() {

        final Long id = 1L;
        final String username = "mjones";
        final String password = "test123";

        Role userRole = RoleBuilder.givenRole().withType(RoleType.ROLE_USER).build();
        Role adminRole = RoleBuilder.givenRole().withType(RoleType.ROLE_ADMIN).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        List<RoleType> newRoles = Collections.singletonList(adminRole.getType());

        UserDto userDto = UserDtoBuilder.givenUserDto().withFirstName("Mike")
                .withLastName("Jones")
                .withUsername(username)
                .withRoleTypes(newRoles)
                .build();

        User user = UserBuilder.givenUser().withId(id).withFirstName("James")
                .withLastName("Smith")
                .withUsername(username)
                .withPassword(password)
                .withRoles(roles)
                .build();

        Assert.assertNotEquals(userDto.getFirstName(), user.getFirstName());
        Assert.assertNotEquals(userDto.getLastName(), user.getLastName());
        Assert.assertNotEquals(userDto.getRoleTypes(), getRoleTypes(user));

        Assert.assertEquals(id, user.getId());
        Assert.assertEquals(username, user.getUsername());
        Assert.assertEquals(password, user.getPassword());

        expectFindUserByUsername(userDto.getUsername(), user);
        expectFindRoleByType(adminRole.getType(), adminRole);
        replayAll();

        userService.update(userDto);
        verifyAll();

        Assert.assertEquals(id, user.getId());
        Assert.assertEquals(username, user.getUsername());
        Assert.assertEquals(password, user.getPassword());

        Assert.assertEquals(userDto.getFirstName(), user.getFirstName());
        Assert.assertEquals(userDto.getLastName(), user.getLastName());
        Assert.assertEquals(userDto.getRoleTypes(), getRoleTypes(user));
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
        User user = UserBuilder.givenUser().withId(1L).withUsername("mjones").build();

        expectFindUserByUsername(user.getUsername(), user);
        expectDeleteUser(user);
        replayAll();

        userService.delete(user.getUsername());
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

    private void expectFindRoleByType(RoleType roleType, Role role) {
        EasyMock.expect(roleRepositoryMock.findByType(roleType))
                .andReturn(role);
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

    private void expectFindRolesByTypeList() {
        EasyMock.expect(roleRepositoryMock.findByTypeIn(EasyMock.anyObject(List.class)))
                .andReturn(new ArrayList());
    }

    private void expectFindRolesByTypeList(List<RoleType> roleTypes, List<Role> roles) {
        EasyMock.expect(roleRepositoryMock.findByTypeIn(roleTypes)).andReturn(roles);
    }

    private List<RoleType> getRoleTypes(User user) {
        return user.getRoles().stream().map(Role::getType).collect(Collectors.toList());
    }
}
