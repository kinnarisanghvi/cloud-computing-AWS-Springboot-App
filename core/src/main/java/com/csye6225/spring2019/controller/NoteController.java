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
    @Autowired
    UserCheck uCheck;

    HttpHeaders responseHeaders = new HttpHeaders();
    HttpServletRequest request;
    HttpServletResponse response;


    @GetMapping("/note")
    public ResponseEntity<Object> getAllNote() throws JSONException, ServletException {
        String auth_user= uCheck.loginUser(request,response);
        if(auth_user.equalsIgnoreCase("Success")) {
            List<Note> notes = noteRepository.findAll();
            List<JSONObject> entities = new ArrayList<JSONObject>();
            for (Note n : notes) {
                JSONObject entity = new JSONObject();
                entity.put("Id", n.getNoteId());
                entity.put("User", n.getUser().getEmailID());
                entity.put("Title", n.getNoteTitle());
                entity.put("Content", n.getNoteContent());
                entity.put("Created At", n.getNoteCreatedAt());
                entity.put("Last Updated At", n.getNoteUpdatedAt());
                entities.add(entity);
            }
            return new ResponseEntity<Object>(entities.toString(), HttpStatus.OK);
        }
        return null;
    }

    @PostMapping("/note")
    public ResponseEntity<Object> newNote(@Valid @RequestBody Note note) {

        long userid = 0L;
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        note.setNoteId(randomUUIDString);
        String user_email = note.getUser().getEmailID();
        User user1 = uRepository.findByEmail(user_email);
        if (user1 != null) {
            userid = user1.getId();

        } else {
            System.out.println("Please enter user");
            return null;
        }
        java.util.Date uDate = new java.util.Date();
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        System.out.println("Time in java.sql.Date is : " + sDate);
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY - hh:mm:ss");
        System.out.println("Using a dateFormat date is : " + df.format(uDate));
        note.setNoteCreatedAt(sDate);
        note.getUser().setId(userid);
        noteRepository.save(note);
        return new ResponseEntity<Object>(note, HttpStatus.CREATED);
    }

    @GetMapping("/note/{id}")
    public ResponseEntity<Object> getOneNote(@PathVariable(value = "id") String id) throws JSONException {
        Optional<Note> note = noteRepository.findById(id);
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put("Id", note.orElseThrow(RuntimeException::new).getNoteId());
        entity.put("User", note.orElseThrow(RuntimeException::new).getUser());
        entity.put("Title", note.orElseThrow(RuntimeException::new).getNoteTitle());
        entity.put("Content", note.orElseThrow(RuntimeException::new).getNoteContent());
        entity.put("Created At", note.orElseThrow(RuntimeException::new).getNoteCreatedAt());
        entity.put("Last Updated At", note.orElseThrow(RuntimeException::new).getNoteUpdatedAt());
        entities.add(entity);
        return new ResponseEntity<Object>(entities.toString(), HttpStatus.FOUND);
    }

    @PutMapping("/note/{id}")
    public ResponseEntity<Object> updateNote(@PathVariable(value = "id") String noteid, @Valid @RequestBody Note note) {

        Note note1 = noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));


        note1.setNoteTitle(note.getNoteTitle());
        note1.setNoteContent(note.getNoteContent());
        java.util.Date uDate1 = new java.util.Date();
        java.sql.Date sDate1 = new java.sql.Date(uDate1.getTime());
        System.out.println("Time in java.sql.Date is : " + sDate1);
        DateFormat df1 = new SimpleDateFormat("dd/MM/YYYY - hh:mm:ss");
        System.out.println("Using a dateFormat date is : " + df1.format(uDate1));
        note.setNoteUpdatedAt(sDate1);
        Note changedNote = noteRepository.save(note1);
        return new ResponseEntity<Object>(changedNote, HttpStatus.MOVED_PERMANENTLY);
    }


    @DeleteMapping("/note/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "id") String noteid) {
        Note note1 = noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));
        noteRepository.delete(note1);
//        return new ResponseEntity<Object>("", HttpStatus.MOVED_PERMANENTLY);
        return new ResponseEntity<String>("{\"message\": \"Deleted\"}", HttpStatus.ACCEPTED);


    }
}