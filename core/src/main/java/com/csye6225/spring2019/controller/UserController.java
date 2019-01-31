package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.utils.Password;
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
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String getUser() {
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
            if(isValid(user1.getEmailID())){
                if(user.getEmailID().equals(user1.getEmailID())){
                    return "Account already exits";
                }else{
                    user.setPassword(Password.hashPassword(user1.getPassword()));
                    userRepository.save(user);
                    return user.getEmailID().toString() + " " + user.getPassword().toString();
                }
            }else{
                return "Invalid password";
            }

        }

        return null;
    }

    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
//    @GetMapping("/notes/{id}")
//    public Note getNoteById(@PathVariable(value = "id") Long noteId) {
//        return userRepository.findById(noteId)
//                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));
//    }
}
