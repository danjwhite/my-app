package com.example.myapp.web.controller;

import com.example.myapp.dto.NoteDTO;
import com.example.myapp.service.NoteManagementService;
import com.example.myapp.web.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final NoteManagementService noteManagementService;

    @GetMapping("/{userGuid}/notes")
    public ResponseEntity<Page<NoteDTO>> getNotesForUser(
            @PageableDefault(size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable, @PathVariable("userGuid") UUID userGuid, @RequestParam(value = "search", required = false) String search) {
        Page<NoteDTO> page = StringUtils.isNotBlank(search) ? noteManagementService.searchNotesForUser(pageable, userGuid, search) :
                noteManagementService.findAllNotesForUser(pageable, userGuid);

        return ResponseFactory.ok(page);
    }

    @GetMapping("/{userGuid}/notes/{noteGuid}")
    public ResponseEntity<NoteDTO> getNoteForUser(@PathVariable("userGuid") UUID userGuid,
                                                  @PathVariable("noteGuid") UUID noteGuid) {
        return ResponseFactory.ok(noteManagementService.findNoteForUser(userGuid, noteGuid));
    }

    @PostMapping("/{userGuid}/notes")
    public ResponseEntity<?> addNoteForUser(@PathVariable("userGuid") UUID userGuid,
                                            @Valid @RequestBody NoteDTO noteDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        noteManagementService.addNoteForUser(userGuid, noteDTO);

        return ResponseFactory.noContent();
    }

    @PutMapping("/{userGuid}/notes/{noteGuid}")
    public ResponseEntity<?> updateNoteForUser(@PathVariable("userGuid") UUID userGuid,
                                               @PathVariable("noteGuid") UUID noteGuid,
                                               @Valid @RequestBody NoteDTO noteDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        noteManagementService.updateNoteForUser(userGuid, noteGuid, noteDTO);

        return ResponseFactory.noContent();
    }

    @DeleteMapping("/{userGuid}/notes/{noteGuid}")
    public ResponseEntity<Void> deleteNoteForUser(@PathVariable("userGuid") UUID userGuid,
                                                  @PathVariable("noteGuid") UUID noteGuid) {
        noteManagementService.deleteNoteForUser(userGuid, noteGuid);

        return ResponseFactory.noContent();
    }
}
