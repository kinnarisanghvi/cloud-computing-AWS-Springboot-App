package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String getDate() {
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        return "{date: "+dateFormat.format(date)+"}";
    }



    @PostMapping("/users/register")
    public User createUser(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

//    @GetMapping("/notes/{id}")
//    public Note getNoteById(@PathVariable(value = "id") Long noteId) {
//        return userRepository.findById(noteId)
//                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));
//    }
}
