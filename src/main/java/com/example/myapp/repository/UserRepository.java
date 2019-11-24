package com.example.myapp.repository;

import com.example.myapp.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(String username);

    User findByGuid(UUID guid);

    @Query(value = "SELECT u FROM User u WHERE u.username LIKE %:search% OR u.firstName LIKE %:search% OR u.lastName LIKE %:search%")
    Page<User> search(Pageable pageable, @Param("search") String search);
}
