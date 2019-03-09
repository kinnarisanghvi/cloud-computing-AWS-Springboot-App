package com.csye6225.spring2019.model;


import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;

@Entity
@Table(name="attachments")
public class Attachment {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name="note_id")
    private Note note;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    }

