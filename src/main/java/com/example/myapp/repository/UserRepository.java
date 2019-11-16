package com.example.myapp.repository;

import com.example.myapp.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(String username);

    @Query(value = "SELECT * FROM user u WHERE u.username LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search%", nativeQuery = true)
    Page<User> search(Pageable pageable, @Param("search") String search);
}
