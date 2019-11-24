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

    @Query(value = "SELECT n FROM Note n WHERE n.user.id = :userId AND (n.title LIKE %:search% OR n.body LIKE %:search%)")
    Page<Note> searchByUserId(Pageable pageable, @Param("userId") long userId, @Param("search") String search);
}
