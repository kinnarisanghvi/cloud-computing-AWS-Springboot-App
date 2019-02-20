package com.csye6225.spring2019.controller;

import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.repository.AttachmentRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class AttachmentController {


    @Autowired
    AttachmentRepository attachmentRepository;

    @GetMapping("/note/{notesid}/attachments") public ResponseEntity<Object> getAllAttachments(@PathVariable(value = "notesid") String notesid, HttpServletRequest request, HttpServletResponse response) throws JSONException {

        List<Attachment> attachment = attachmentRepository.findByNoteId(notesid);
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        //if (note.get().getUser().getId() == Long.valueOf(auth_user_1[1])) {
            for(Attachment att:attachment) {
                entity.put("Id", att.getAttachmentId());
                entity.put("Url", att.getUrl());
                entities.add(entity);
            }
            //  entity.put("attachments",note.orElseThrow(RuntimeException::new).getAttachment());

            return new ResponseEntity<Object>(entities.toString(), HttpStatus.FOUND);


    }



}
