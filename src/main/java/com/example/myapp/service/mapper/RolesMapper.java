package com.example.myapp.service.mapper;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolesMapper {

    private final RoleRepository roleRepository;

    public Set<Role> mapToRoles(List<RoleType> source) {
        return new HashSet<>(roleRepository.findByTypeIn(source));
    }

    public Set<Role> mapToRoles(List<RoleType> source, Set<Role> target) {
        List<RoleType> targetRoles = mapToRoleTypes(target);
        target.removeIf(r -> !source.contains(r.getType()));

        List<RoleType> newRoles = source.stream().filter(r -> !targetRoles.contains(r)).collect(Collectors.toList());
        target.addAll(roleRepository.findByTypeIn(newRoles));

        return target;
    }

    public List<RoleType> mapToRoleTypes(Set<Role> roles) {
        return roles.stream().map(Role::getType).collect(Collectors.toList());
    }
}
