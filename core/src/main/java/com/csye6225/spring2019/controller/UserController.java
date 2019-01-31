package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public String getDate(@FormParam("name") String name, @FormParam("password") String password, @Context HttpServletResponse servletResponse) {

        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        List<User> users = userRepository.findAll();

        Iterator itr = users.iterator();
        while (itr.hasNext()){
            User u = (User)itr.next();
            if(u.getEmailID().equalsIgnoreCase(name) && u.getPassword().equalsIgnoreCase(password)) {
                return "{date: "+dateFormat.format(date)+"}";

            }
            else {
                return "{message: Please provide proper credentials}";

            }
        }




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
