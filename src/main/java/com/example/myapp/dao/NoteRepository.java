package com.example.myapp.dao;

import com.example.myapp.domain.Note;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NoteRepository extends CrudRepository<Note, Long> {

    List<Note> findAllByUserId(long userId);

    List<Note> findTop10ByUserIdOrderByCreatedAtDesc(long userId);

    long countByUserId(long userId);
}
