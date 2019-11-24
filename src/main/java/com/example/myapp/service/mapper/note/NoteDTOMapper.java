package com.example.myapp.service.mapper.note;

import com.example.myapp.domain.Note;
import com.example.myapp.dto.NoteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteDTOMapper {

    public NoteDTO mapToNoteDTO(Note source) {
        NoteDTO dto = new NoteDTO();
        dto.setGuid(source.getGuid());
        dto.setCreatedAt(source.getCreatedAt());
        dto.setTitle(source.getTitle());
        dto.setBody(source.getBody());

        return dto;
    }

    public Note mapToNote(NoteDTO source) {
        Note note = new Note();
        note.setTitle(source.getTitle());
        note.setBody(source.getBody());

        return note;
    }

    public void mapToNote(NoteDTO source, Note target) {
        target.setTitle(source.getTitle());
        target.setBody(source.getBody());
    }
}
