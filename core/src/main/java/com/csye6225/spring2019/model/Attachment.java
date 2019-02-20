package com.csye6225.spring2019.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Attachment {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String attachmentId;

    @Column(name = "url")
    private String url;


    public String getAttachmentId() {
        return attachmentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }
}
