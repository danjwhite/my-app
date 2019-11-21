package com.example.myapp.dto;

import com.example.myapp.constraint.BlankCheck;
import com.example.myapp.constraint.SizeCheck;
import com.example.myapp.domain.Note;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Data
@GroupSequence({NoteDTO.class, BlankCheck.class, SizeCheck.class})
public class NoteDTO {

    private UUID guid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    @Size(min = 1, max = 140, message = "Title must be within 140 characters.", groups = SizeCheck.class)
    private String title;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    @Size(min = 1, max = 5000, message = "Body must be within 5,000 characters.", groups = SizeCheck.class)
    private String body;
}
