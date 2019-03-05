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
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
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
                        entity.put("Id", n.getId());
                        entity.put("User", n.getUser().getEmailID());
                        entity.put("Title", n.getTitle());
                        entity.put("Content", n.getContent());
                        entity.put("Created At", n.getCreated_on());
                        entity.put("Last Updated At", n.getUpdated_on());
                        for(int i=0;i<n.getAttachmentList().size();i++) {
                                        entity.put("attachments",n.getAttachmentList().get(i));
                        }
                        entities.add(entity);
                    }

                }
                return new ResponseEntity<Object>(entities.toString(), HttpStatus.OK);

            }
        }
        return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/note")
    public ResponseEntity<Object> newNote(@Valid @RequestBody Note note,HttpServletRequest request,HttpServletResponse response) {

        auth_user= uCheck.loginUser(request,response,uRepository);
        if(auth_user == "0") {

            //returns when id or password is nukk
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        } else if(auth_user == "1") {

            //returns when password is wrong
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if(auth_user == "2") {
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
            System.out.println("Using a dateFormat date is : " + df.format(uDate));
            note.setCreated_on(sDate);
            //note.getUser().setId(userid);
            note.setUser(user);
            noteRepository.save(note);
            return new ResponseEntity<Object>(note, HttpStatus.CREATED);
        }
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/note/{id}")
    public ResponseEntity<Object> getOneNote(@PathVariable(value = "id") String id,HttpServletRequest request,HttpServletResponse response) throws JSONException {

        //handles the case where noteId is not passed
        try{

            String noteId = id;
            if(id==null){
                System.out.println("NoteId required");
            }
        }catch(NullPointerException e){
            return new ResponseEntity<Object>("{\"NoteId required\"}", HttpStatus.BAD_REQUEST);

        }

        auth_user = uCheck.loginUser(request, response, uRepository);
        if(auth_user == "0") {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        } else if(auth_user == "1") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if(auth_user == "2") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");

            if (auth_user_1[0].equalsIgnoreCase("Success")) {
                Optional<Note> note = noteRepository.findById(id);

                //checking if note_id is present in db
                if(note.equals(null)){
                    return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
                }
                List<JSONObject> entities = new ArrayList<JSONObject>();
                JSONObject entity = new JSONObject();
                if (note.get().getUser().getId() == Long.valueOf(auth_user_1[1])) {
                    entity.put("id", note.orElseThrow(RuntimeException::new).getId());
                    entity.put("content", note.orElseThrow(RuntimeException::new).getContent());
                    entity.put("title", note.orElseThrow(RuntimeException::new).getTitle());
                    entity.put("created_on", note.orElseThrow(RuntimeException::new).getCreated_on());
                    entity.put("last_updated_on", note.orElseThrow(RuntimeException::new).getUpdated_on());

                    entities.add(entity);
                    return new ResponseEntity<Object>(entities.toString(), HttpStatus.OK);
                } else {

                    //this will be returned if user is not authorized on the note
                    return new ResponseEntity<Object>( HttpStatus.UNAUTHORIZED);
                }

            } else {
                return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/note/{id}")
    public ResponseEntity<Object> updateNote(@PathVariable(value = "id") String noteid, @Valid @RequestBody Note note,HttpServletRequest request,HttpServletResponse response) throws JSONException {

        Note note1;
        //handles the case where noteId is not passed
        try{

            String noteId = noteid;
            if(noteId==null){
                System.out.println("NoteId required");
            }
        }catch(NullPointerException e){
            return new ResponseEntity<Object>("{\"NoteId required\"}", HttpStatus.BAD_REQUEST);

        }

        auth_user = uCheck.loginUser(request, response, uRepository);
        if(auth_user == "0") {
            return new ResponseEntity<Object>("{\"Username and password required\"}", HttpStatus.BAD_REQUEST);
        } else if(auth_user == "1") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if(auth_user == "2") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);}
        else {
            auth_user_1 = auth_user.split(",");

            try {
                note1 = noteRepository.findById(noteid).orElseThrow(() -> new NullPointerException());
                if(note1.equals(null)){
                    System.out.println();
                }
            }catch (Exception e){
                return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);}

            }

            List<JSONObject> entities = new ArrayList<JSONObject>();
            if (auth_user_1[0].equalsIgnoreCase("Success") && note1.getUser().getId() == Long.valueOf(auth_user_1[1])) {

                note1.setTitle(note.getTitle());
                note1.setContent(note.getContent());
                java.util.Date uDate1 = new java.util.Date();
                java.sql.Date sDate1 = new java.sql.Date(uDate1.getTime());
                System.out.println("Time in java.sql.Date is : " + sDate1);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                System.out.println("Using a dateFormat date is : " + df1.format(uDate1));
                note1.setUpdated_on(sDate1);
                noteRepository.save(note1);
                return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping("/note/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "id") String noteid,HttpServletRequest request,HttpServletResponse response) {

        Note note1;

        //handles the case where noteId is not passed
        try{

            String noteId = noteid;
            if(noteId==null){
                System.out.println("NoteId required");
            }
        }catch(NullPointerException e){
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);

        }


        auth_user = uCheck.loginUser(request, response, uRepository);
        if(auth_user == "0") {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        } else if(auth_user == "1") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else if(auth_user == "2") {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        } else {
            auth_user_1 = auth_user.split(",");
            try {
                note1 = noteRepository.findById(noteid).orElseThrow(() -> new NullPointerException());
                if(note1.equals(null)){
                    System.out.println();
                }
            }catch (Exception e){
                return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);}

        }
            if (auth_user_1[0].equalsIgnoreCase("Success") && note1.getUser().getId() == Long.valueOf(auth_user_1[1])) {
                noteRepository.delete(note1);

                for(int i=0;i< note1.getAttachmentList().size();i++) {
                    attachmentRepository.deleteById(note1.getAttachmentList().get(i).getAttachmentId());
                }

                return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        }
    }
}