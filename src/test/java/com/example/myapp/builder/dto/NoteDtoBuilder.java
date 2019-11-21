package com.example.myapp.builder.dto;

import com.example.myapp.builder.AbstractBuilder;
import com.example.myapp.dto.NoteDTO;

public class NoteDtoBuilder extends AbstractBuilder<NoteDTO> {

    private NoteDtoBuilder() {
        super(new NoteDTO());
    }

    public static NoteDtoBuilder givenNoteDto() {
        return new NoteDtoBuilder();
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
