package com.example.myapp.service;

import com.example.myapp.domain.Note;
import com.example.myapp.dto.NoteDTO;
import org.springframework.stereotype.Component;

@Component
public class NoteDTOMapper {

    public NoteDTO map(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setGuid(note.getGuid());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setTitle(note.getTitle());
        dto.setBody(note.getBody());

        return dto;
    }
}
