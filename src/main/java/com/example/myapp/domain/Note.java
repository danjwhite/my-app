package com.example.myapp.domain;

import com.example.myapp.constraint.BlankCheck;
import com.example.myapp.constraint.SizeCheck;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.GroupSequence;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "note")
@GroupSequence({Note.class, BlankCheck.class, SizeCheck.class})
public class Note implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "title")
    @NotEmpty(message = "Cannot be blank", groups = BlankCheck.class)
    @Size(min = 1, max = 140, message = "Title must be within 140 characters.", groups = SizeCheck.class)
    private String title;

    @Column(name = "body")
    @NotEmpty(message = "Cannot be blank", groups = BlankCheck.class)
    @Size(min = 1, max = 5000, message = "Body must be within 5,000 characters.", groups = SizeCheck.class)
    private String body;

    public Note() {
    }

    public Note(Date createdAt, String title, String body) {
        this(null, createdAt, title, body);
    }

    public Note(Long id, Date createdAt, String title, String body) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that, "id", "createdAt");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id", "createdAt");
    }
}
