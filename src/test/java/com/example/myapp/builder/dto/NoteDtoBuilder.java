package com.example.myapp.builder.dto;

import com.example.myapp.builder.AbstractBuilder;
import com.example.myapp.dto.NoteDto;

public class NoteDtoBuilder extends AbstractBuilder<NoteDto> {

    private NoteDtoBuilder() {
        super(new NoteDto());
    }

    public static NoteDtoBuilder givenNoteDto() {
        return new NoteDtoBuilder();
    }

    public NoteDtoBuilder withNoteId(Long noteId) {
        getObject().setId(noteId);
        return this;
    }

    public NoteDtoBuilder withUsername(String username) {
        getObject().setUsername(username);
        return this;
    }

    public NoteDtoBuilder withTitle(String title) {
        getObject().setTitle(title);
        return this;
    }

    public NoteDtoBuilder withBody(String body) {
        getObject().setBody(body);
        return this;
    }
}
