package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import javax.ws.rs.Produces;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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


    @Produces("application/json")
    @PostMapping("/users/register")
    public String createUser(@Valid @RequestBody User user) {

        List<User> users = userRepository.findAll();
        for(User user1 : users){

            if(user.getEmailID().equals(user1.getEmailID())){
                return "Account already exits";
            }else{
                userRepository.save(user);
                return user.getEmailID().toString() + " " + user.getPassword().toString();

            }
        }

        return null;
    }

//    @GetMapping("/notes/{id}")
//    public Note getNoteById(@PathVariable(value = "id") Long noteId) {
//        return userRepository.findById(noteId)
//                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));
//    }
}
