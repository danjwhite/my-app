package com.example.myapp.editor;

import com.example.myapp.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component
public class RoleEditor extends PropertyEditorSupport {

    @Autowired
    private IRoleService roleService;

    @Override
    public void setAsText(String id) {

        setValue(roleService.findById(Long.parseLong(id)));
    }
}
