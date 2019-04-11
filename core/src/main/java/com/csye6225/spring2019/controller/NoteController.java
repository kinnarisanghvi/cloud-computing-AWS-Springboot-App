package com.csye6225.spring2019.controller;

import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.AttachmentRepository;
import com.csye6225.spring2019.repository.NoteRepository;
import com.csye6225.spring2019.repository.UserRepository;
import javax.servlet.http.HttpServletResponse;

import com.csye6225.spring2019.utils.AmazonClient;
import com.csye6225.spring2019.utils.UserCheck;
import javax.servlet.http.HttpServletRequest;

import com.timgroup.statsd.StatsDClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
public class NoteController {

    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository uRepository;
    @Autowired
    AttachmentRepository attachmentRepository;

    UserCheck uCheck = new UserCheck();
    String auth_user = null;
    String[] auth_user_1 = new String[3];

    private AmazonClient amazonClient;

    HttpHeaders responseHeaders = new HttpHeaders();

    @Autowired
    private StatsDClient statsd;

    private final static Logger LOG = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    NoteController(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/note")
    public ResponseEntity<Object> getAllNote(HttpServletRequest request, HttpServletResponse response) throws JSONException, ServletException {

        LOG.info("Inside getAllNote()");
        statsd.incrementCounter("/note url hit");
        if(LOG.isTraceEnabled()){
            LOG.trace(">> getAllNote()");
        }

        auth_user = uCheck.loginUser(request, response, uRepository);
        if (auth_user == "0") {
            LOG.error("Bad request");
            return new ResponseEntity<Object>("unauthorized", HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            LOG.warn("Wrong password");
            return new ResponseEntity<Object>("unauthorized", HttpStatus.FORBIDDEN);
        } else if (auth_user == "2") {
            LOG.warn("Bad request");
            return new ResponseEntity<Object>("unauthorized", HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");
            if (auth_user_1[0].equalsIgnoreCase("Success")) {
                List<Note> notes = noteRepository.findAll();
                List<JSONObject> entities = new ArrayList<JSONObject>();
                for (Note n : notes) {
                    if (n.getUser().getId() == Long.valueOf(auth_user_1[1])) {
                        JSONObject entity = new JSONObject();
                        entity.put("Id", n.getId());
                        entity.put("Content", n.getContent());
                        entity.put("Title", n.getTitle());
                        entity.put("Created_on", n.getCreated_on());
                        entity.put("Last_updated_on", n.getLast_updated_on());
                        JSONObject attachmentobj = new JSONObject();
                        for (int i = 0; i < n.getAttachmentList().size(); i++) {
                            attachmentobj.put("id", n.getAttachmentList().get(i).getId());
                            attachmentobj.put("url",n.getAttachmentList().get(i).getUrl());
                        }
                        entity.put("attachment", attachmentobj);
                        entities.add(entity);
                    }

                }
                LOG.info("User created "+ entities.toString());
                return new ResponseEntity<Object>(entities.toString(), HttpStatus.OK);

            }
        }
        LOG.warn("Unauthorized User "+ auth_user_1[1]);

        return new ResponseEntity<Object>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/note")
    public ResponseEntity<Object> newNote(@Valid @RequestBody Note note, HttpServletRequest request, HttpServletResponse response) throws JSONException {
        LOG.info("Inside newNote()");
        statsd.incrementCounter("/note url hit");
        if(LOG.isTraceEnabled()){
            LOG.trace(">> newNote()");
        }
        auth_user = uCheck.loginUser(request, response, uRepository);
        if (auth_user == "4") {
            LOG.warn("Bad request");
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        } else if (auth_user == "0") {
            LOG.warn("Bad request : Request without credentails ");
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            LOG.warn("Bad request : Wrong password");
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            LOG.warn("Bad request");
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");
            long userid = 0L;
            final String randomUUIDString = UUID.randomUUID().toString().replace("-", "");
            note.setId(randomUUIDString);
            userid = Long.valueOf(auth_user_1[1]);
            User user = new User();
            user.setId(userid);
            System.out.print("note id random "+ randomUUIDString);
            java.util.Date uDate = new java.util.Date();
            java.sql.Date sDate = new java.sql.Date(uDate.getTime());
            System.out.println("Time in java.sql.Date is : " + sDate);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String created_on = df.format(sDate);
            System.out.println("Using a dateFormat date is : " + df.format(uDate));
            note.setCreated_on(created_on);
            note.setUser(user);
            noteRepository.save(note);
            List<JSONObject> entities = new ArrayList<JSONObject>();
            JSONObject entity = new JSONObject();
            if (note.getUser().getId().equals(Long.valueOf(auth_user_1[1]))) {
                LOG.info("verified user");
                entity.put("Id", note.getId());
                System.out.print("note id "+ note.getId());
                entity.put("Content", note.getContent());
                entity.put("Title", note.getTitle());
                entity.put("Created_on", note.getCreated_on());
                entity.put("Last_updated_on", note.getLast_updated_on());
                entity.put("attachments", note.getAttachmentList());

                entities.add(entity);
                LOG.info("Added note" +entities.toString());
                return new ResponseEntity<>(entities.toString(), HttpStatus.CREATED);
            } else {
                LOG.info("Could not verify user?" + user);
                LOG.info("note.getUser().getId() ==> " + note.getUser().getId() + "; value of auth_user_1 ==>" + Long.valueOf(auth_user_1[1]) + "; userid ==>" + userid);
                return new ResponseEntity<Object>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }
        }        
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/note/{idNotes}")
    public ResponseEntity<Object> getOneNote(@PathVariable(value = "idNotes") String id, HttpServletRequest request, HttpServletResponse response) throws JSONException {

        LOG.info("Inside getOneNote()");
        statsd.incrementCounter("/note/{idNotes} url hit");
        if(LOG.isTraceEnabled()){
            LOG.trace(">> getOneNote()");
        }
        Note note = noteRepository.findBy(id);
        System.out.println("note :"+ note);
        if (note.equals(null)) {
            LOG.error("Note id is not found");
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.contains("Basic")) {
            String userDetails[] = new String[2];
            assert header.substring(0, 6).equals("Basic");
            String basicAuthEncoded = header.substring(6);
            String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
            userDetails = basicAuthAsString.split(":", 2);
            String email = userDetails[0];
            System.out.println("userdetails : "+ userDetails[0]);
            User userExists = uRepository.findByEmail(email);
            System.out.println("user exists: "+ userExists);


            auth_user = uCheck.loginUser(request, response, uRepository);
            System.out.println("auth user in update: "+ auth_user);
            auth_user_1 = auth_user.split(",");
            if (auth_user == "4") {
                LOG.info("Bad request");
                return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
            } else if (auth_user == "0") {
                LOG.warn("Bad request");
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "1") {
                LOG.warn("Bad request : Wrong password");
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "2") {
                LOG.warn("Bad request");
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            } else if (!(note.getUser().getEmailID().equals(email))) {
                LOG.warn("User in not authorized on this note");
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            }
            List<JSONObject> entities = new ArrayList<JSONObject>();
            JSONObject entity = new JSONObject();
            if (note.getUser().getId().equals(Long.valueOf(auth_user_1[1]))) {
                entity.put("Id", note.getId());
                entity.put("Content", note.getContent());
                entity.put("Title", note.getTitle());
                entity.put("Created_on", note.getCreated_on());
                entity.put("Last_updated_on", note.getLast_updated_on());
                JSONObject attachmentobj = new JSONObject();
                for (int i = 0; i < note.getAttachmentList().size(); i++) {
                    attachmentobj.put("id", note.getAttachmentList().get(i).getId());
                    attachmentobj.put("url",note.getAttachmentList().get(i).getUrl());
                }
                entity.put("attachment", attachmentobj);
                entities.add(entity);
                LOG.info("Returning note" +entities.toString());
                return new ResponseEntity<>(entities.toString(), HttpStatus.OK);
            }
        }
        return null;
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/note/{idNotes}")
    public ResponseEntity<Object> updateNote(@PathVariable(value = "idNotes") String id, @Valid @RequestBody Note note, HttpServletRequest request, HttpServletResponse response, UserRepository ur) throws JSONException {

        LOG.info("Inside updateNote()");
        statsd.incrementCounter("/note/{idNotes} url for put hit");
        Note updated_note = noteRepository.getOne(id);
        System.out.println("update note: " + updated_note);
        if (updated_note.equals(null)) {
            LOG.warn("Bad request : Request does not have noteID" );
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.contains("Basic")) {
            String userDetails[] = new String[2];
            assert header.substring(0, 6).equals("Basic");
            String basicAuthEncoded = header.substring(6);
            String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
            userDetails = basicAuthAsString.split(":", 2);

            String email = userDetails[0];

            auth_user = uCheck.loginUser(request, response, uRepository);

            if (auth_user == "4") {
                LOG.warn("Bad request");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (auth_user == "0") {
                LOG.warn("Bad request : No credentails found");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "1") {
                LOG.warn("Bad request : Wrong password");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "2") {
                LOG.warn("Bad request");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (!(updated_note.getUser().getEmailID().equals(email))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            auth_user_1 = auth_user.split(",");
            List<JSONObject> entities = new ArrayList<JSONObject>();
            System.out.println("auth user update note: " + auth_user_1[0] + " " + auth_user_1[1]);
            if (auth_user_1[0].equalsIgnoreCase("Success") && updated_note.getUser().getId() == Long.valueOf(auth_user_1[1])) {
                if (note.getTitle() != null) {
                    updated_note.setTitle(note.getTitle());
                    updated_note.setContent(note.getContent());
                    java.util.Date uDate1 = new java.util.Date();
                    java.sql.Date sDate1 = new java.sql.Date(uDate1.getTime());
                    System.out.println("Time in java.sql.Date is : " + sDate1);
                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    String updated_date = df1.format(sDate1);
                    System.out.println("Using a dateFormat date is : " + df1.format(uDate1));
                    updated_note.setLast_updated_on(updated_date);
                    Note changedNote = noteRepository.save(updated_note);
                    JSONObject entity = new JSONObject();
                    entity.put("id", changedNote.getId());
                    entity.put("title", changedNote.getTitle());
                    entity.put("content", changedNote.getContent());
                    entity.put("created_on", changedNote.getCreated_on());
                    entity.put("Last Updated At", changedNote.getLast_updated_on());
                    if(note.getAttachmentList().size()==0){
                        entity.put("attachments",note.getAttachmentList());
                    }
                    else {
                        for (int i = 0; i < note.getAttachmentList().size(); i++) {
                            entity.put("attachments", note.getAttachmentList().get(i));

                        }
                    }
                    entities.add(entity);
                    LOG.info("Updated note" +entities.toString());
                    return new ResponseEntity<>(entities.toString(), HttpStatus.OK
                    );
                }
            }

        }
        LOG.warn("Bad request");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @DeleteMapping("/note/{idNotes}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "idNotes") String noteid, HttpServletRequest request, HttpServletResponse response) {

        LOG.info("Inside deleteNote()");
        statsd.incrementCounter("/note/{idNotes} url hit");
        if(LOG.isTraceEnabled()){
            LOG.trace(">> loginUser()");
        }
        Note delete_note = noteRepository.getOne(noteid);
        if (delete_note.equals(null)) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
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
            if (auth_user == "4") {
                LOG.warn("Bad request");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (auth_user == "0") {
                LOG.warn("Bad request : No credetails found");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "1") {
                LOG.warn("Bad request : Wrong password");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "2") {
                LOG.warn("Bad request");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (!(delete_note.getUser().getEmailID().equals(email))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else {
                auth_user_1 = auth_user.split(",");

                if (auth_user_1[0].equalsIgnoreCase("Success") && delete_note.getUser().getId() == Long.valueOf(auth_user_1[1])) {


                    for (int i = 0; i < delete_note.getAttachmentList().size(); i++) {
                        this.amazonClient.deleteFileFromS3Bucket(delete_note.getAttachmentList().get(i).getUrl());
                        attachmentRepository.deleteById(delete_note.getAttachmentList().get(i).getId());

                    }

                    noteRepository.delete(delete_note);
                    LOG.info("Deleted note");
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }
        }
        LOG.warn("Bad request");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
