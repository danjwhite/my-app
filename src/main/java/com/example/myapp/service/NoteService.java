package com.example.myapp.service;

import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDTO;
import com.example.myapp.repository.NoteRepository;
import com.example.myapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteDTOMapper noteDTOMapper;

    @Transactional
    public Page<NoteDTO> findAllForUser(Pageable pageable, UUID userGuid) {
        User user = getUser(userGuid);
        Page<Note> page = noteRepository.findAllByUserId(pageable, user.getId());

        return page.map(noteDTOMapper::map);
    }

    @Transactional
    public Page<NoteDTO> searchForUser(Pageable pageable, UUID userGuid, String search) {
        User user = getUser(userGuid);
        Page<Note> page = noteRepository.searchByUserId(pageable, user.getId(), search);

        return page.map(noteDTOMapper::map);
    }

    @Transactional
    public NoteDTO findForUser(UUID userGuid, UUID noteGuid) {
        return noteDTOMapper.map(findByUserGuidAndGuid(userGuid, noteGuid));
    }

    @Transactional
    public void addNoteForUser(UUID userGuid, NoteDTO noteDTO) {
        User user = getUser(userGuid);

        Note note = new Note();
        note.setUser(user);
        note.setTitle(noteDTO.getTitle());
        note.setBody(noteDTO.getBody());

        noteRepository.save(note);
    }

    @Transactional
    public void updateNoteForUser(UUID userGuid, UUID guid, NoteDTO noteDTO) {
        Note note = findByUserGuidAndGuid(userGuid, guid);
        note.setTitle(noteDTO.getTitle());
        note.setBody(noteDTO.getBody());
    }

    @Transactional
    public void deleteNoteForUser(UUID userGuid, UUID guid) {
        noteRepository.delete(findByUserGuidAndGuid(userGuid, guid));
    }

    private Note findByUserGuidAndGuid(UUID userGuid, UUID guid) {
        User user = getUser(userGuid);
        Note note = noteRepository.findByUserIdAndGuid(user.getId(), guid);

        if (note == null) {
            throw new EntityNotFoundException(
                    String.format("Note not found for userGuid=%s and guid=%s", userGuid.toString(), guid.toString()));
        }

        return note;
    }

    private User getUser(UUID guid) {
        User user = userRepository.findByGuid(guid);

        if (user == null) {
            throw new EntityNotFoundException("User not found for guid=" + guid);
        }

        return user;
    }
}
