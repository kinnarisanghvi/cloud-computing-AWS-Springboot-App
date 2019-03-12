package com.csye6225.spring2019.controller;

import com.csye6225.spring2019.exception.ResourceNotFoundException;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.AttachmentRepository;
import com.csye6225.spring2019.repository.NoteRepository;
import com.csye6225.spring2019.repository.UserRepository;
import javax.servlet.http.HttpServletResponse;
import com.csye6225.spring2019.utils.UserCheck;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.JSONArray;
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

    HttpHeaders responseHeaders = new HttpHeaders();

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/note")
    public ResponseEntity<Object> getAllNote(HttpServletRequest request, HttpServletResponse response) throws JSONException, ServletException {
        auth_user = uCheck.loginUser(request, response, uRepository);
        if (auth_user == "0") {
            return new ResponseEntity<Object>("unauthorized", HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            return new ResponseEntity<Object>("unauthorized", HttpStatus.FORBIDDEN);
        } else if (auth_user == "2") {
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
                return new ResponseEntity<Object>(entities.toString(), HttpStatus.OK);

            }
        }
        return new ResponseEntity<Object>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/note")
    public ResponseEntity<Object> newNote(@Valid @RequestBody Note note, HttpServletRequest request, HttpServletResponse response) throws JSONException {

        auth_user = uCheck.loginUser(request, response, uRepository);
        if (auth_user == "4") {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        } else if (auth_user == "0") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "1") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if (auth_user == "2") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");
            long userid = 0L;
            UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();
            note.setId(randomUUIDString);
            userid = Long.valueOf(auth_user_1[1]);
            User user = new User();
            user.setId(userid);
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
            if (note.getUser().getId() == Long.valueOf(auth_user_1[1])) {
                entity.put("Id", note.getId());
                entity.put("Content", note.getContent());
                entity.put("Title", note.getTitle());
                entity.put("Created_on", note.getCreated_on());
                entity.put("Last_updated_on", note.getLast_updated_on());
                for (int i = 0; i < note.getAttachmentList().size(); i++) {
                    entity.put("attachments", note.getAttachmentList().get(i));
                }
                entities.add(entity);
                return new ResponseEntity<>(entities.toString(), HttpStatus.CREATED);
            }
        }
        return null;
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/note/{idNotes}")
    public ResponseEntity<Object> getOneNote(@PathVariable(value = "idNotes") String id, HttpServletRequest request, HttpServletResponse response) throws JSONException {

        Note note = noteRepository.findBy(id);
        System.out.println("note :"+ note);
        if (note.equals(null)) {
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
                return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
            } else if (auth_user == "0") {
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "1") {
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "2") {
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            } else if (!(note.getUser().getEmailID().equals(email))) {
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            }
            List<JSONObject> entities = new ArrayList<JSONObject>();
            JSONObject entity = new JSONObject();
            if (note.getUser().getId() == Long.valueOf(auth_user_1[1])) {
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
                return new ResponseEntity<>(entities.toString(), HttpStatus.OK);
            }
        }
        return null;
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/note/{idNotes}")
    public ResponseEntity<Object> updateNote(@PathVariable(value = "idNotes") String id, @Valid @RequestBody Note note, HttpServletRequest request, HttpServletResponse response, UserRepository ur) throws JSONException {

        Note updated_note = noteRepository.getOne(id);
        System.out.println("update note: "+ updated_note);
        if (updated_note.equals(null)) {
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
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (auth_user == "0") {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "1") {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "2") {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (!(updated_note.getUser().getEmailID().equals(email))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            auth_user_1 = auth_user.split(",");
            List<JSONObject> entities = new ArrayList<JSONObject>();
            System.out.println("auth user update note: "+ auth_user_1[0]+ " "+ auth_user_1[1]);
            if (auth_user_1[0].equalsIgnoreCase("Success") && updated_note.getUser().getId() == Long.valueOf(auth_user_1[1])) {

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
                for (int i = 0; i < note.getAttachmentList().size(); i++) {
                    entity.put("attachments", note.getAttachmentList().get(i));
                }

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        return null;
    }

    @DeleteMapping("/note/{idNotes}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "idNotes") String noteid, HttpServletRequest request, HttpServletResponse response) {

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
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (auth_user == "0") {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "1") {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (auth_user == "2") {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (!(delete_note.getUser().getEmailID().equals(email))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else {
                auth_user_1 = auth_user.split(",");

                if (auth_user_1[0].equalsIgnoreCase("Success") && delete_note.getUser().getId() == Long.valueOf(auth_user_1[1])) {


                    for (int i = 0; i < delete_note.getAttachmentList().size(); i++) {
                        attachmentRepository.deleteById(delete_note.getAttachmentList().get(i).getId());
                    }

                    noteRepository.delete(delete_note);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}