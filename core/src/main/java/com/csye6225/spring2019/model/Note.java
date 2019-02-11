package com.csye6225.spring2019.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;


@Entity
@Table(name="notes")

public class Note {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="noteid")
    private Integer noteId;


    @Column(name="note_title")
    private String noteTitle;


    @Column(name="note_content")
    private String noteContent;

    @Column(name="note_createdAt")
    private Date noteCreatedAt;

    @Column(name="note_lastUpdated")
    private Date noteUpdatedAt;

    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public Date getNoteCreatedAt() {
        return noteCreatedAt;
    }

    public void setNoteCreatedAt(Date noteCreatedAt) {
        this.noteCreatedAt = noteCreatedAt;
    }

    public Date getNoteUpdatedAt() {
        return noteUpdatedAt;
    }

    public void setNoteUpdatedAt(Date noteUpdatedAt) {
        this.noteUpdatedAt = noteUpdatedAt;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @ManyToOne
    @JoinColumn(name="user_id")
    private User userId;
    }

