package com.example.myapp.service;

import com.example.myapp.domain.Note;
import com.example.myapp.dto.NoteDTO;
import com.example.myapp.service.mapper.note.NoteDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteManagementService {

    private final NoteService noteService;
    private final UserService userService;
    private final NoteDTOMapper noteDTOMapper;

    @Transactional
    public Page<NoteDTO> findAllNotesForUser(Pageable pageable, UUID userGuid) {
        Page<Note> page = noteService.findAllForUser(pageable, getUserId(userGuid));

        return page.map(noteDTOMapper::mapToNoteDTO);
    }

    @Transactional
    public Page<NoteDTO> searchNotesForUser(Pageable pageable, UUID userGuid, String search) {
        Page<Note> page = noteService.searchForUser(pageable, getUserId(userGuid), search);

        return page.map(noteDTOMapper::mapToNoteDTO);
    }

    @Transactional
    public NoteDTO findNoteForUser(UUID userguid, UUID noteGuid) {
        return noteDTOMapper.mapToNoteDTO(findForUser(userguid, noteGuid));
    }

    @Transactional
    public void addNoteForUser(UUID userGuid, NoteDTO noteDTO) {
        Note note = noteDTOMapper.mapToNote(noteDTO);
        note.setUser(userService.findByGuid(userGuid));

        noteService.save(note);
    }

    @Transactional
    public void updateNoteForUser(UUID userGuid, UUID noteGuid, NoteDTO noteDTO) {
        Note note = findForUser(userGuid, noteGuid);
        noteDTOMapper.mapToNote(noteDTO, note);
    }

    @Transactional
    public void deleteNoteForUser(UUID userGuid, UUID noteGuid) {
        noteService.delete(findForUser(userGuid, noteGuid));
    }

    private Note findForUser(UUID userGuid, UUID noteGuid) {
        return noteService.findForUser(getUserId(userGuid), noteGuid);
    }

    private long getUserId(UUID userGuid) {
        return userService.findByGuid(userGuid).getId();
    }
}
