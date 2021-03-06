package com.example.myapp.dto;

import com.example.myapp.constraint.BlankCheck;
import com.example.myapp.constraint.SizeCheck;
import com.example.myapp.domain.Note;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@GroupSequence({NoteDto.class, BlankCheck.class, SizeCheck.class})
public class NoteDto {

    private Long id;

    private String username;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    @Size(min = 1, max = 140, message = "Title must be within 140 characters.", groups = SizeCheck.class)
    private String title;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    @Size(min = 1, max = 5000, message = "Body must be within 5,000 characters.", groups = SizeCheck.class)
    private String body;

    public NoteDto(Note note) {
        id = note.getId();
        username = note.getUser().getUsername();
        title = note.getTitle();
        body = note.getBody();
    }
}
