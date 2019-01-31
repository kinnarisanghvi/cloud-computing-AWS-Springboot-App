package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.utils.Password;
import com.csye6225.spring2019.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.apache.commons.validator.routines.EmailValidator;
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

                if(user.getEmailID().equals(user1.getEmailID())){
                    return "Account already exits";
                }else if(isValidEmailAddress(user1.getEmailID())){
//                    boolean valid = EmailValidator.getInstance().isValid(user1.getEmailID());
//                    if(valid) {
                        user.setPassword(Password.hashPassword(user1.getPassword()));
                        userRepository.save(user);
                        return user.getEmailID().toString() + " " + user.getPassword().toString();
//                    }else{
//                        return "INvalid emailId";
//                    }
                }else{
                    return "Invalid Email";
                }
        }

        return null;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

//    @GetMapping("/notes/{id}")
//    public Note getNoteById(@PathVariable(value = "id") Long noteId) {
//        return userRepository.findById(noteId)
//                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));
//    }
}
