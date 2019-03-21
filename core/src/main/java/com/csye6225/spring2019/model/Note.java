package com.csye6225.spring2019.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="notes")

public class Note {

    @Id
    @Column(name = "note_id")
    private String id;

    @Column(name = "note_title")
    private String title;

    @Column(name = "note_content")
    private String content;

    @Column(name = "note_createdAt", columnDefinition = "text")
    private String created_on;

    public void setLast_updated_on(String last_updated_on) {
        this.last_updated_on = last_updated_on;
    }

    @Column(name = "note_lastUpdated", columnDefinition = "text")
    private String last_updated_on;

    public String getLast_updated_on() {
        return last_updated_on;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "note")
    private List<Attachment> attachmentList = new ArrayList<>();

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}