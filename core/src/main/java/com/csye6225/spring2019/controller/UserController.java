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
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.SignatureAlgorithm;


import javax.servlet.ServletException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
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
    HttpHeaders responseHeaders = new HttpHeaders();


    @RequestMapping("/")
    public ResponseEntity<String> loginSuccess() {
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        responseHeaders.set("MyResponseHeader", "MyValue");
        return new ResponseEntity<String>("date:" +dateFormat.format(date), responseHeaders, HttpStatus.ACCEPTED);
    }


    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<String> login(@RequestBody User login) throws ServletException {
        String jwtToken = "";

        if (login.getEmailID() == null || login.getPassword() == null) {
            throw new ServletException("Please fill in username and password");
        }

        String email = login.getEmailID();
        String password = login.getPassword();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ServletException("User email not found.");
        }

        String pwd = user.getPassword();

        if (!password.equals(pwd)) {
            throw new ServletException("Invalid login. Please check your name and password.");
        }

        jwtToken = Jwts.builder().setSubject(email).claim("roles", "user").setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey").compact();
        //                return "{message: Please provide proper credentials}";

//        return "{'token': '" + jwtToken + "'}";
        return new ResponseEntity<String>("token: " + jwtToken + "}", responseHeaders, HttpStatus.ACCEPTED);
    }






    @Produces("application/json")
    @PostMapping("/users/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {

        List<User> users = userRepository.findAll();
        for(User user1 : users){

                if(user.getEmailID().equals(user1.getEmailID())){
                    return new ResponseEntity<String>("Account already exits",responseHeaders,HttpStatus.CONFLICT);
                }else if(isValidEmailAddress(user1.getEmailID())){
//                    boolean valid = EmailValidator.getInstance().isValid(user1.getEmailID());
//                    if(valid) {
                        user.setPassword(Password.hashPassword(user1.getPassword()));
                        userRepository.save(user);
                        return new ResponseEntity<String>(user.getEmailID().toString(),responseHeaders,HttpStatus.OK);
                }else{
                    return new ResponseEntity<String>("Invalid Email", responseHeaders, HttpStatus.NOT_ACCEPTABLE);
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


}
