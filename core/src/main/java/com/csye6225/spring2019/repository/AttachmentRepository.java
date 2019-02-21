package com.csye6225.spring2019.repository;

import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {

    public default List<Attachment> findBy(String id) {
        List<Attachment> attachmentList = this.findAll();
        Attachment foundattachment = null;
        for (Attachment attachment1 : attachmentList) {
            System.out.println("attachmentrepo "+ attachment1);
            if (attachment1.getAttachmentId().equals(id)){
                foundattachment = attachment1;
                System.out.println("foundattachment " + foundattachment);
                break;
            }
        }

        Note note = foundattachment.getNote();
        String note_id = note.getNoteId();
        List<Attachment> attachments = this.findAll();
        for (Attachment attachment1 : attachmentList) {
            System.out.println("attachmentrepo "+ attachment1);
            if (attachment1.getNote().getNoteId().equals(note_id)){
                attachments.add(attachment1);

            }
        }

        return attachments;
    }


}
