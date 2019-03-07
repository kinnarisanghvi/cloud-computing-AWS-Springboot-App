package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.repository.NoteRepository;
import com.csye6225.spring2019.repository.UserRepository;
import com.csye6225.spring2019.service.AttachmentStorageService;
import com.csye6225.spring2019.utils.UserCheck;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@RestController
@Profile("local")
public class AttachmentLocalStorageController {

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    UserRepository uRepository;


    @Autowired
    AttachmentStorageService attachmentStorageService;

    UserCheck uCheck = new UserCheck();
    String auth_user = null;
    String[] auth_user_1 = new String[3];

    @PostMapping("/note/{idNotes}/attachments")
    public ResponseEntity<Object> newAttachment(@PathVariable(value = "idNotes") String idNotes, @RequestPart(value = "file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws JSONException {
        auth_user = uCheck.loginUser(request, response, uRepository);
        auth_user_1 = auth_user.split(",");
        if (auth_user == "0") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {


            Note note = noteRepository.getOne(idNotes);
            if (note.getUser().getId() != Long.valueOf(auth_user_1[1])) {
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            } else {
                Attachment attachment = attachmentStorageService.storeFile(file,note);

                List<JSONObject> entities = new ArrayList<JSONObject>();
                JSONObject entity = new JSONObject();

                entity.put("id", attachment.getId());
                entity.put("url", attachment.getUrl());
                entities.add(entity);


                return new ResponseEntity<Object>(entities.toString(), HttpStatus.OK);
            }
        }
    }

    @DeleteMapping("/note/{idNotes}/attachments/{idAttachments}")
    public ResponseEntity<Object> deleteAttachment(@PathVariable(value = "idNotes") String idNotes, HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "idAttachments") String idAttachments) {

        auth_user = uCheck.loginUser(request, response, uRepository);
        auth_user_1 = auth_user.split(",");
        Note note = noteRepository.getOne(idNotes);
        if (auth_user == "0") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (note.equals(null)) {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {


            if (note.getUser().getId() != Long.valueOf(auth_user_1[1])) {
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            }
            // String attachmentID = attachment.getAttachmentId();
            else {
                if(attachmentStorageService.deleteFile(idAttachments)){
                    return new ResponseEntity<Object>(HttpStatus.OK);
                }
            }
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);

        }
    }


    @PutMapping("/note/{idNotes}/attachments/{idAttachments}")
    public ResponseEntity<Object> updateAttachment(@PathVariable(value = "idNotes") String idNotes, @RequestPart(value = "file") MultipartFile file, HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "idAttachments") String idAttachments) throws JSONException {

        auth_user = uCheck.loginUser(request, response, uRepository);
        auth_user_1 = auth_user.split(",");

        if (auth_user == "0") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {



                Note note = noteRepository.getOne(idNotes);
                if (note.getUser().getId() != Long.valueOf(auth_user_1[1])) {
                    return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
                }
                // String attachmentID = attachment.getAttachmentId();
                else {

                    attachmentStorageService.updateAttachment(idAttachments, file);


                    return new ResponseEntity<Object>(HttpStatus.OK);
                }
        }
    }

    @GetMapping("/note/{idNotes}/attachments")
    public ResponseEntity<Object> getAllAttachments(@PathVariable(value = "idNotes") String idNotes, HttpServletRequest request, HttpServletResponse response) throws JSONException {

        Note note = noteRepository.findBy(idNotes);
        if (note.equals(null)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        auth_user = uCheck.loginUser(request, response, uRepository);
        if (auth_user == "0") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {

            auth_user_1 = auth_user.split(",");
            if (auth_user_1[0].equalsIgnoreCase("Success")) {

                if (note.getUser().getId() == Long.valueOf(auth_user_1[1])) {
                    List<JSONObject> entities = new ArrayList<JSONObject>();


                    for (Attachment att : note.getAttachmentList()) {
                        JSONObject entity = new JSONObject();
                        entity.put("Id", att.getId());
                        entity.put("Url", att.getUrl());
                        entities.add(entity);
                    }


                    //  entity.put("attachments",note.orElseThrow(RuntimeException::new).getAttachment());

                    return new ResponseEntity<Object>(entities.toString(), HttpStatus.OK);


                }

            }
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);

        }
    }
}
