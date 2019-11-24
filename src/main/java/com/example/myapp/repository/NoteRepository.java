package com.example.myapp.repository;

import com.example.myapp.domain.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NoteRepository extends PagingAndSortingRepository<Note, Long> {

    Note findByUserIdAndGuid(long userId, UUID guid);

    Page<Note> findAllByUserId(Pageable pageable, long userId);

    @Query(value = "SELECT * FROM note n WHERE user_id = :userId AND (title LIKE %:search% OR body LIKE %:search%)", nativeQuery = true)
    Page<Note> searchByUserId(Pageable pageable, @Param("userId") long userId, @Param("search") String search);
}
