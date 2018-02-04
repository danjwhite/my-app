package com.example.myapp.dto;

import com.example.myapp.constraint.BlankCheck;
import com.example.myapp.constraint.SizeCheck;
import com.example.myapp.domain.Note;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.GroupSequence;
import javax.validation.constraints.Size;

@GroupSequence({Note.class, BlankCheck.class, SizeCheck.class})
public class NoteDto {

    private Long noteId;

    private String username;

    @NotEmpty(message = "Cannot be blank", groups = BlankCheck.class)
    @Size(min = 1, max = 140, message = "Title must be within 140 characters.", groups = SizeCheck.class)
    private String title;

    @NotEmpty(message = "Cannot be blank", groups = BlankCheck.class)
    @Size(min = 1, max = 5000, message = "Body must be within 5,000 characters.", groups = SizeCheck.class)
    private String body;

    public NoteDto() {
    }

    public NoteDto(Note note) {
        noteId = note.getId();
        username = note.getUser().getUsername();
        title = note.getTitle();
        body = note.getBody();
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long id) {
        this.noteId = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
