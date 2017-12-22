package com.example.myapp.dao;

import com.example.myapp.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class JdbcNoteRepository implements NoteRepository {

    private JdbcOperations jdbc;

    @Autowired
    public JdbcNoteRepository(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Note> findNotes() {
        String sql = "select id, title, created_at, body " +
                "from note " +
                "order by id desc";

        return jdbc.query(sql, new NoteRowMapper());
    }

    @Override
    public List<Note> findRecentNotes(int count) {
        String sql = "select id, title, created_at, body " +
                "from note " +
                "order by id desc limit ?";

        return jdbc.query(sql, new NoteRowMapper(), count);
    }

    @Override
    public Note findOne(long id) {
        String sql = "select id, title, created_at, body " +
                "from note " +
                "where id = ?";

        return jdbc.queryForObject(sql, new NoteRowMapper(), id);
    }

    @Override
    public Long save(final Note note) {
        final String sql = "insert into note (title, created_at, body) " +
                "values (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
                ps.setString(1, note.getTitle());
                ps.setTimestamp(2, new Timestamp(note.getCreatedAt().getTime()));
                ps.setString(3, note.getBody());

                return ps;
            }
        };

        jdbc.update(preparedStatementCreator, keyHolder);

        return (Long) keyHolder.getKey();
    }

    private static class NoteRowMapper implements RowMapper<Note> {
        public Note mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Note(
                    rs.getLong("id"),
                    rs.getDate("created_at"), rs.getString("title"),
                    rs.getString("body")
            );
        }
    }
}
