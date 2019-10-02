package com.example.myapp.service;

import com.example.myapp.builder.entity.RoleBuilder;
import com.example.myapp.dao.RoleRepository;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import org.easymock.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(EasyMockRunner.class)
public class RoleServiceTest extends EasyMockSupport {

    @Mock(type = MockType.STRICT)
    private RoleRepository roleRepositoryMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RoleService roleService;

    @Before
    public void setUp() {
        roleService = new RoleService(roleRepositoryMock);
    }

    @Test
    public void findByIdShouldReturnExpectedResult() {
        long roleId = 1L;
        Role role = RoleBuilder.givenRole().withId(roleId).build();

        expectFindById(roleId, role);
        replayAll();

        Role result = roleService.findById(roleId);
        verifyAll();

        Assert.assertEquals(role, result);
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionWhenRoleNotFound() {
        long roleId = 1L;

        expectedException.expect(EntityNotFoundException.class);
        expectedException.expectMessage("Role not found for id: " + roleId);

        expectFindById(roleId, null);
        replayAll();

        roleService.findById(roleId);
        verifyAll();
    }

    @Test
    public void findByTypeShouldReturnExpectedResult() {
        RoleType type = RoleType.ROLE_USER;
        Role role = RoleBuilder.givenRole().withId(1L).build();

        expectFindByType(type, role);
        replayAll();

        Role result = roleService.findByType(type);
        verifyAll();

        Assert.assertEquals(role, result);
    }

    @Test
    public void findAllShouldReturnExpectedResult() {
        List<Role> roles = Collections.singletonList(RoleBuilder.givenRole().withId(1L).build());

        expectFindAll(roles);
        replayAll();

        List<Role> result = roleService.findAll();
        verifyAll();

        Assert.assertEquals(roles, result);
    }

    private void expectFindById(long id, Role role) {
        EasyMock.expect(roleRepositoryMock.findById(id))
                .andReturn(Optional.ofNullable(role));
    }

    private void expectFindByType(RoleType type, Role role) {
        EasyMock.expect(roleRepositoryMock.findByType(type))
                .andReturn(role);
    }

    private void expectFindAll(List<Role> roles) {
        EasyMock.expect(roleRepositoryMock.findAll())
                .andReturn(roles);
    }
}
