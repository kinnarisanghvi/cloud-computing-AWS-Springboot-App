package com.csye6225.spring2019.controller;

import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.AttachmentRepository;
import com.csye6225.spring2019.repository.NoteRepository;
import com.csye6225.spring2019.repository.UserRepository;
import com.csye6225.spring2019.utils.AmazonClient;
import com.csye6225.spring2019.utils.UserCheck;
import com.timgroup.statsd.StatsDClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.*;

@Configuration
@RestController
@Profile("dev")
public class AttachmentController {

    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository uRepository;
    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    private StatsDClient statsd;

    private final static Logger LOG = LoggerFactory.getLogger(AttachmentController.class);



    UserCheck uCheck = new UserCheck();
    String auth_user = null;
    String[] auth_user_1 = new String[3];

    private AmazonClient amazonClient;

    @Autowired
    AttachmentController(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Profile("dev")
    @GetMapping("/note/{idNotes}/attachments")
    public ResponseEntity<Object> getAllAttachments(@PathVariable(value = "idNotes") String idNotes, HttpServletRequest request, HttpServletResponse response) throws JSONException {

        LOG.info("Inside getAllAttachments()");
        statsd.incrementCounter("/note/{idNotes}/attachments url hit");
        if(LOG.isTraceEnabled()){
            LOG.trace(">> getAllAttachments()");
        }
        Note note = noteRepository.findBy(idNotes);
        if (note.equals(null)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.contains("Basic")) {
            String userDetails[] = new String[2];
            assert header.substring(0, 6).equals("Basic");
            String basicAuthEncoded = header.substring(6);
            String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
            userDetails = basicAuthAsString.split(":", 2);

            User userExists = uRepository.findByEmail(userDetails[0]);
            String email = userDetails[0];

        auth_user = uCheck.loginUser(request, response, uRepository);
        if (auth_user == "0") {
            LOG.warn("Bad request : No credentials passed");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            LOG.warn("Bad request : Wrong password");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            LOG.warn("Bad request");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {

            auth_user_1 = auth_user.split(",");
            if (auth_user_1[0].equalsIgnoreCase("Success")) {

                if (note.getUser().getId() == Long.valueOf(auth_user_1[1])) {
                    List<JSONObject> entities = new ArrayList<JSONObject>();

                    JSONObject entity = new JSONObject();

                    for (Attachment att : note.getAttachmentList()) {
                        entity.put("Id", att.getId());
                        entity.put("Url", att.getUrl());
                        entities.add(entity);
                    }

                    return new ResponseEntity<>(entities.toString(), HttpStatus.OK);

                }

            }
            LOG.warn("Bad request");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        }
        return null;
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Profile("dev")
    @PostMapping("/note/{idNotes}/attachments")
    public ResponseEntity<Object> newAttachment(@PathVariable(value = "idNotes") String idNotes, @RequestPart(value = "file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws JSONException {

        LOG.info("Inside newAttachment()");
        statsd.incrementCounter("/note/{idNotes}/attachments url for post hit");
        Note note = noteRepository.getOne(idNotes);
        if (note.equals(null)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.contains("Basic")) {
            String userDetails[] = new String[2];
            assert header.substring(0, 6).equals("Basic");
            String basicAuthEncoded = header.substring(6);
            String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
            userDetails = basicAuthAsString.split(":", 2);

            User userExists = uRepository.findByEmail(userDetails[0]);
            String email = userDetails[0];
            String password = userDetails[1];

            if(email.equals(null) || password.equals(null)){
                LOG.warn("Bad request : No credetials");
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            }
        }

        auth_user = uCheck.loginUser(request, response, uRepository);
        auth_user_1 = auth_user.split(",");
        if (auth_user == "0") {
            LOG.warn("Bad request : No credentials");
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            LOG.warn("Bad request : Wrong password");
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            LOG.warn("Bad request");
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {

            String fileName = new Date().getTime() + "-" + file.getOriginalFilename().replace(" ", "_");

            if (note.getUser().getId() != Long.valueOf(auth_user_1[1])) {
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            } else {

                String url = this.amazonClient.uploadFile(file);
                UUID uuid = UUID.randomUUID();
                String randomUUIDString = uuid.toString();
                Attachment attachment = new Attachment();
                attachment.setId(randomUUIDString);
                attachment.setUrl(url);
                attachment.setNote(note);
                attachment.getNote().setId(idNotes);

                attachmentRepository.save(attachment);

                List<JSONObject> entities = new ArrayList<JSONObject>();
                JSONObject entity = new JSONObject();

                entity.put("id", attachment.getId());
                entity.put("url", attachment.getUrl());
                entities.add(entity);
                LOG.info("Added attachment" +entities.toString());
                return new ResponseEntity<Object>(entities.toString(), HttpStatus.OK);
            }
        }
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Profile("dev")
    @PutMapping("/note/{idNotes}/attachments/{idAttachments}")
    public ResponseEntity<Object> updateAttachment(@PathVariable(value = "idNotes") String idNotes,  @PathVariable(value = "idAttachments") String idAttachments,@RequestPart(value = "file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws JSONException {

        LOG.info("Inside updateAttachment()");
        statsd.incrementCounter("/note/{idNotes}/attachments/{idAttachments} hit");
        if(LOG.isTraceEnabled()){
            LOG.trace(">> updateAttachment()");
        }
        Attachment attachment = null;
        Note note = noteRepository.getOne(idNotes);
        if (note.equals(null)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Attachment> attachmentList = note.getAttachmentList();
        for (Attachment a : attachmentList) {
            if (a.getId().equals(idAttachments)) {
                attachment = a;
                break;
            } else {
                LOG.warn("Bad request : Note does not contain this attachment");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }


        String header = request.getHeader("Authorization");
        if (header != null && header.contains("Basic")) {
            String userDetails[] = new String[2];
            assert header.substring(0, 6).equals("Basic");
            String basicAuthEncoded = header.substring(6);
            String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
            userDetails = basicAuthAsString.split(":", 2);

            User userExists = uRepository.findByEmail(userDetails[0]);
            String email = userDetails[0];
            String password = userDetails[1];

            if(email.equals(null) || password.equals(null)){
                LOG.warn("Bad request : Bad credentials");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }

        auth_user = uCheck.loginUser(request, response, uRepository);
        auth_user_1 = auth_user.split(",");

        if (auth_user == "0") {
            LOG.warn("Bad request : No credentials");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            LOG.warn("Bad request : Wrong password");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            LOG.warn("Bad request");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (note.getUser().getId() != Long.valueOf(auth_user_1[1])) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
                    String url = attachment.getUrl();
                    this.amazonClient.deleteFileFromS3Bucket(url);


                    String url1 = this.amazonClient.uploadFile(file);

                    attachment.setUrl(url1);
                    attachment.getNote().setId(idNotes);

                    attachmentRepository.save(attachment);
            LOG.info("updated attachment" +attachment.getId());
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping("/note/{idNotes}/attachments/{idAttachments}")
    public ResponseEntity<Object> deleteAttachment(@PathVariable(value = "idNotes") String idNotes, HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "idAttachments") String idAttachments) {

        LOG.info("Inside deleteAttachment()");
        statsd.incrementCounter("/note/{idNotes}/attachments url hit");
        Attachment attachment = null;
        Note note = noteRepository.getOne(idNotes);
        if (note.equals(null)) {
            LOG.warn("Bad request : No such note");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Attachment> attachmentList = note.getAttachmentList();
        for (Attachment a : attachmentList) {
            if (a.getId().equals(idAttachments)) {
                attachment = a;
                break;
            } else {
                LOG.warn("Bad request : attachment ID is not found for this note");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.contains("Basic")) {
            String userDetails[] = new String[2];
            assert header.substring(0, 6).equals("Basic");
            String basicAuthEncoded = header.substring(6);
            String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
            userDetails = basicAuthAsString.split(":", 2);

            User userExists = uRepository.findByEmail(userDetails[0]);
            String email = userDetails[0];
            String password = userDetails[1];

            if(email.equals(null) || password.equals(null)){
                LOG.warn("Bad request : No credentials found");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }

        auth_user = uCheck.loginUser(request, response, uRepository);
        auth_user_1 = auth_user.split(",");

        if (auth_user == "0") {
            LOG.warn("Bad request : No credentials");
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            LOG.warn("Bad request : Password incorrect");
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            LOG.warn("Bad request");
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {

            if (note.getUser().getId() != Long.valueOf(auth_user_1[1])) {
                LOG.warn("Bad request : User is not authorized on this note");
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            }
            else {

                String url = attachment.getUrl();
                this.amazonClient.deleteFileFromS3Bucket(url);
                attachmentRepository.delete(attachment);
            }

            LOG.info("Deleted attachment");
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }
    }

}