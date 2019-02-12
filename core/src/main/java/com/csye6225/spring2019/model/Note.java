package com.csye6225.spring2019.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name="notes")

public class Note {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "noteid")
    private String noteId;

    @Column(name = "note_title")
    private String noteTitle;

    @Column(name = "note_content")
    private String noteContent;

    @Column(name = "note_createdAt", columnDefinition = "text")
    private Date noteCreatedAt;

    public Date getNoteCreatedAt() {
        return noteCreatedAt;
    }

    public Date getNoteUpdatedAt() {
        return noteUpdatedAt;
    }

    public void setNoteUpdatedAt(Date noteUpdatedAt) {
        this.noteUpdatedAt = noteUpdatedAt;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }


    public void setNoteCreatedAt(Date noteCreatedAt) {
        this.noteCreatedAt = noteCreatedAt;
    }

    @Column(name = "note_lastUpdated", columnDefinition = "text")
    private Date noteUpdatedAt;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}