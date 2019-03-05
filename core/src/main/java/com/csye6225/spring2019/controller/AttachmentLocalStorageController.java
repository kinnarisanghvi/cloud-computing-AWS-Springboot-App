package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.repository.NoteRepository;
import com.csye6225.spring2019.repository.UserRepository;
import com.csye6225.spring2019.service.AttachmentStorageService;
import com.csye6225.spring2019.utils.UserCheck;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;


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
                Attachment attachment = attachmentStorageService.storeFile(file);


                return new ResponseEntity<Object>(attachment, HttpStatus.OK);
            }
        }
    }



}
