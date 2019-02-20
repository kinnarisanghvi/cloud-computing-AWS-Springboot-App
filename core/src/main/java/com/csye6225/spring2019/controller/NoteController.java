package com.csye6225.spring2019.controller;

import com.csye6225.spring2019.exception.ResourceNotFoundException;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.NoteRepository;
import com.csye6225.spring2019.repository.UserRepository;
import javax.servlet.http.HttpServletResponse;
import com.csye6225.spring2019.utils.UserCheck;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
public class NoteController {

    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository uRepository;

    UserCheck uCheck = new UserCheck();
    String auth_user=null;
    String [] auth_user_1=new String[3];

    HttpHeaders responseHeaders = new HttpHeaders();

    @GetMapping("/note")
    public ResponseEntity<Object> getAllNote(HttpServletRequest request, HttpServletResponse response) throws JSONException, ServletException {
        auth_user= uCheck.loginUser(request,response,uRepository);
        if(auth_user == "0") {
            return new ResponseEntity<Object>("{\"message\": \"Invalid Login\"}", HttpStatus.NOT_ACCEPTABLE);
        } else if(auth_user == "1") {
            return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.FORBIDDEN);
        } else if(auth_user == "2") {
            return new ResponseEntity<Object>("{\"message\": \"Incorrect Authorization Headers\"}", HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");
            if (auth_user_1[0].equalsIgnoreCase("Success")) {
                List<Note> notes = noteRepository.findAll();
                List<JSONObject> entities = new ArrayList<JSONObject>();
                for (Note n : notes) {
                    if (n.getUser().getId() == Long.valueOf(auth_user_1[1])) {
                        JSONObject entity = new JSONObject();
                        entity.put("Id", n.getNoteId());
                        entity.put("User", n.getUser().getEmailID());
                        entity.put("Title", n.getNoteTitle());
                        entity.put("Content", n.getNoteContent());
                        entity.put("Created At", n.getNoteCreatedAt());
                        entity.put("Last Updated At", n.getNoteUpdatedAt());
                        entity.put("attachments",n.getAttachment());
                        entities.add(entity);
                    }

                }
                return new ResponseEntity<Object>(entities.toString(), HttpStatus.OK);

            }
        }
        return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/note")
    public ResponseEntity<Object> newNote(@Valid @RequestBody Note note,HttpServletRequest request,HttpServletResponse response) {

        auth_user= uCheck.loginUser(request,response,uRepository);
        if(auth_user == "0") {
            return new ResponseEntity<Object>("{\"message\": \"Invalid Login\"}", HttpStatus.NOT_ACCEPTABLE);
        } else if(auth_user == "1") {
            return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.FORBIDDEN);
        } else if(auth_user == "2") {
            return new ResponseEntity<Object>("{\"message\": \"Incorrect Authorization Headers\"}", HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");
            long userid = 0L;
            UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();
            note.setNoteId(randomUUIDString);
            userid = Long.valueOf(auth_user_1[1]);
            User user = new User();
            user.setId(userid);
            java.util.Date uDate = new java.util.Date();
            java.sql.Date sDate = new java.sql.Date(uDate.getTime());
            System.out.println("Time in java.sql.Date is : " + sDate);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            System.out.println("Using a dateFormat date is : " + df.format(uDate));
            note.setNoteCreatedAt(sDate);
//            note.getUser().setId(userid);
            note.setUser(user);
            noteRepository.save(note);
            return new ResponseEntity<Object>(note, HttpStatus.CREATED);
        }
    }

    @GetMapping("/note/{id}")
    public ResponseEntity<Object> getOneNote(@PathVariable(value = "id") String id,HttpServletRequest request,HttpServletResponse response) throws JSONException {
        auth_user = uCheck.loginUser(request, response, uRepository);
        if(auth_user == "0") {
            return new ResponseEntity<Object>("{\"message\": \"Invalid Login\"}", HttpStatus.NOT_ACCEPTABLE);
        } else if(auth_user == "1") {
            return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.FORBIDDEN);
        } else if(auth_user == "2") {
            return new ResponseEntity<Object>("{\"message\": \"Incorrect Authorization Headers\"}", HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");

            if (auth_user_1[0].equalsIgnoreCase("Success")) {
                Optional<Note> note = noteRepository.findById(id);
                List<JSONObject> entities = new ArrayList<JSONObject>();
                JSONObject entity = new JSONObject();
                if (note.get().getUser().getId() == Long.valueOf(auth_user_1[1])) {
                    entity.put("Id", note.orElseThrow(RuntimeException::new).getNoteId());
                    entity.put("User", note.orElseThrow(RuntimeException::new).getUser());
                    entity.put("Title", note.orElseThrow(RuntimeException::new).getNoteTitle());
                    entity.put("Content", note.orElseThrow(RuntimeException::new).getNoteContent());
                    entity.put("Created At", note.orElseThrow(RuntimeException::new).getNoteCreatedAt());
                    entity.put("Last Updated At", note.orElseThrow(RuntimeException::new).getNoteUpdatedAt());
                    entity.put("attachments",note.orElseThrow(RuntimeException::new).getAttachment());
                    entities.add(entity);
                    return new ResponseEntity<Object>(entities.toString(), HttpStatus.FOUND);
                } else {
                    return new ResponseEntity<Object>("{\"message\": \"User Does not exist in db\"}", HttpStatus.UNAUTHORIZED);
                }

            } else {
                return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @PutMapping("/note/{id}")
    public ResponseEntity<Object> updateNote(@PathVariable(value = "id") String noteid, @Valid @RequestBody Note note,HttpServletRequest request,HttpServletResponse response) throws JSONException {

        auth_user = uCheck.loginUser(request, response, uRepository);
        if(auth_user == "0") {
            return new ResponseEntity<Object>("{\"message\": \"Invalid Login\"}", HttpStatus.NOT_ACCEPTABLE);
        } else if(auth_user == "1") {
            return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.FORBIDDEN);
        } else if(auth_user == "2") {
            return new ResponseEntity<Object>("{\"message\": \"Incorrect Authorization Headers\"}", HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");

            Note note1 = noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));
            List<JSONObject> entities = new ArrayList<JSONObject>();
            if (auth_user_1[0].equalsIgnoreCase("Success") && note1.getUser().getId() == Long.valueOf(auth_user_1[1])) {

                note1.setNoteTitle(note.getNoteTitle());
                note1.setNoteContent(note.getNoteContent());
                java.util.Date uDate1 = new java.util.Date();
                java.sql.Date sDate1 = new java.sql.Date(uDate1.getTime());
                System.out.println("Time in java.sql.Date is : " + sDate1);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                System.out.println("Using a dateFormat date is : " + df1.format(uDate1));
                note1.setNoteUpdatedAt(sDate1);
                Note changedNote = noteRepository.save(note1);
                JSONObject entity = new JSONObject(); entity.put("Id", changedNote.getNoteId());
                entity.put("User", changedNote.getUser().getEmailID());
                entity.put("Title", changedNote.getNoteTitle());
                entity.put("Content", changedNote.getNoteContent());
                entity.put("Created At", changedNote.getNoteCreatedAt());
                entity.put("Last Updated At", changedNote.getNoteUpdatedAt());
                entity.put("attachments",changedNote.getAttachment());
                entities.add(entity);


                return new ResponseEntity<Object>(entities.toString(), HttpStatus.MOVED_PERMANENTLY);
            }
            return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/note/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "id") String noteid,HttpServletRequest request,HttpServletResponse response) {

        auth_user = uCheck.loginUser(request, response, uRepository);
        if(auth_user == "0") {
            return new ResponseEntity<Object>("{\"message\": \"Invalid Login\"}", HttpStatus.NOT_ACCEPTABLE);
        } else if(auth_user == "1") {
            return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.FORBIDDEN);
        } else if(auth_user == "2") {
            return new ResponseEntity<Object>("{\"message\": \"Incorrect Authorization Headers\"}", HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");
            Note note1 = noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));

            if (auth_user_1[0].equalsIgnoreCase("Success") && note1.getUser().getId() == Long.valueOf(auth_user_1[1])) {
                noteRepository.delete(note1);
                return new ResponseEntity<String>("{\"message\": \"Deleted\"}", HttpStatus.ACCEPTED);
            }
            return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.UNAUTHORIZED);
        }
    }
}