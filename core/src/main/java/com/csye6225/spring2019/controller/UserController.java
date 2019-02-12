package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.utils.Password;
import com.csye6225.spring2019.repository.UserRepository;
//import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.SignatureAlgorithm;


import javax.servlet.ServletException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;
    HttpHeaders responseHeaders = new HttpHeaders();

    @RequestMapping(value="/", method = RequestMethod.GET)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginSuccess(@RequestBody User user) throws ServletException{
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        if (user.getEmailID() == null || user.getPassword() == null) {
            throw new ServletException("Please fill in username and password");
        }

        String email = user.getEmailID();
        String password = user.getPassword();
        System.out.println(email + "  "+ password);

        User user1 = userRepository.findByEmail(email);

        if (user1 == null) {
            throw new ServletException("User email not found.");
        }

        boolean flag = Password.checkPassword(user.getPassword(),user1.getPassword());
        // long userid = user1.getId();

        if (!flag) {
            throw new ServletException("Invalid login. Please check your name and password.");
        }
        responseHeaders.set("MyResponseHeader", "MyValue");
        //        "{\"message\":
        return new ResponseEntity<String>("{\"date\": \"" + dateFormat.format(date) + "\"}", responseHeaders, HttpStatus.ACCEPTED);
    }



    @Produces("application/json")
    @PostMapping("/users/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        List<String> errorList = new ArrayList<String>();
        List<User> users = userRepository.findAll();
        for(User user1 : users) {

            if (user.getEmailID().equals(user1.getEmailID())) {
                return new ResponseEntity<String>("{\"message\":\"Account already exits\"}", responseHeaders, HttpStatus.CONFLICT);
            }
        }
        if(isValidEmailAddress(user.getEmailID())){
            if(isValidPassword(user.getPassword(),errorList)) {
                user.setPassword(Password.hashPassword(user.getPassword()));
                userRepository.save(user);
                return new ResponseEntity<String>("{\"message\": \"" + "Account created Successfully." + "\"}".toString(), responseHeaders, HttpStatus.OK);
            } else {
                if(!errorList.isEmpty()) {
                 return new ResponseEntity<String>("{\"message\": \"" + errorList.toString() + "\"}",responseHeaders,HttpStatus.BAD_REQUEST);
                }
            }
        } else {
            return new ResponseEntity<String>("{\"message\": \"Invalid Email\"}", responseHeaders, HttpStatus.NOT_ACCEPTABLE);

        }


        return null;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean isValidPassword(String passwordhere, List<String> errorList) {

        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");
        errorList.clear();

        boolean flag=true;

        if (passwordhere.length() < 8) {
            errorList.add("Password length must have atleast 8 character !!");
            flag=false;
        }
        if (!specailCharPatten.matcher(passwordhere).find()) {
            errorList.add("Password must have atleast one special character !!");
            flag=false;
        }
        if (!UpperCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password must have atleast one uppercase character !!");
            flag=false;
        }
        if (!lowerCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password must have atleast one lowercase character !!");
            flag=false;
        }
        if (!digitCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password must have atleast one digit character !!");
            flag=false;
        }

        return flag;

    }


}
