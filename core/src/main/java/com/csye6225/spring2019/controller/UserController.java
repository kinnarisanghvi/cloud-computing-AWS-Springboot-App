package com.csye6225.spring2019.controller;
import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.utils.AmazonClient;
import com.csye6225.spring2019.utils.Password;
import com.csye6225.spring2019.repository.UserRepository;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;


@RestController
public class UserController {

    private AmazonClient amazonClient;
    @Autowired
    UserRepository userRepository;
    HttpHeaders responseHeaders = new HttpHeaders();

    @Autowired
    private StatsDClient statsd;

    private final static Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserController(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/reset")
    public ResponseEntity<String> resetPassword(@RequestBody String stringToParse) {
        LOG.info("Inside resetPassword()");
        statsd.incrementCounter("/reset url hit");
        HashMap<String, String> map = new Gson().fromJson(stringToParse, new TypeToken<HashMap<String, String>>() {
        }.getType());

        String username = map.get("email");
        List<User> users = userRepository.findAll();
        try {

            if (username.equals(null) && username.length() > 0 && isValidEmailAddress(username)) {
                System.out.println("Username is null");
                return new ResponseEntity<String>("{\"message\":\"Username is null or invalid\"}", responseHeaders, HttpStatus.FORBIDDEN);

            }
        } catch (NullPointerException e) {
            LOG.error("Bad request");
            return new ResponseEntity<String>("{\"message\":\"Username is null or invalid\"}", responseHeaders, HttpStatus.FORBIDDEN);
        }
        for (User user1 : users) {
            try {
                if (username.equals(user1.getEmailID())) {
                    amazonClient.publishSNSTopic("Email", username);
                    return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);

                }
            } catch (NullPointerException e) {
                LOG.warn("Bad request");
                return new ResponseEntity<String>("{\"message\":\"User not registered\"}", responseHeaders, HttpStatus.FORBIDDEN);
            }

        }
        return new ResponseEntity<String>("{\"message\":\"User not registered\"}", responseHeaders, HttpStatus.FORBIDDEN);

    }

    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> loginUser(HttpServletRequest request, HttpServletResponse response) {
        LOG.info("Inside loginUser()");

        statsd.incrementCounter("/ url hit");
        if(LOG.isTraceEnabled()){
            LOG.trace(">> loginUser()");
        }

        final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();
        String date = sdf.format(cal.getTime());
        String header = request.getHeader("Authorization");
        if (header != null && header.contains("Basic")) {
            String userDetails[] = new String[2];
            assert header.substring(0, 6).equals("Basic");
            String basicAuthEncoded = header.substring(6);
            String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
            userDetails = basicAuthAsString.split(":", 2);
            User userExists = userRepository.findByEmail(userDetails[0]);
            try {
                String email = userDetails[0];
                String password = userDetails[1];
                if(email.equals(null) || password.equals(null)){
                    System.out.println("Username or password is null");

                }
                System.out.println(email + "  " + password);
            }catch (NullPointerException e){
                LOG.warn("Bad request");
                return new ResponseEntity<String>("{\"message\":\"Enter username and password\"}", responseHeaders, HttpStatus.FORBIDDEN);

            }

            if (userExists == null) {
                LOG.warn("Invalid username and password");
                return new ResponseEntity<String>("{\"Message\": \"User not found.\"}", responseHeaders, HttpStatus.BAD_REQUEST);
            }

            boolean flag = Password.checkPassword(userDetails[1], userExists.getPassword());
            // long userid = user1.getId();

            if (!flag) {
                return new ResponseEntity<String>("{\"Message\": \"Invalid Login.\"}", responseHeaders, HttpStatus.NOT_ACCEPTABLE);
            }
            responseHeaders.set("MyResponseHeader", "MyValue");
            //        "{\"message\":
            LOG.info("User returned:"+userDetails[1]);
            return new ResponseEntity<String>("{\"date\": \"" + date + "\"}", responseHeaders, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<String>("{\"Message\": \"Please use Basic Auth with credentials.\"}", responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }

    @Produces("application/json")
    @PostMapping("/user/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        LOG.info("Inside createUser()");
        statsd.incrementCounter("/user/register url hit");
        List<String> errorList = new ArrayList<String>();
        List<User> users = userRepository.findAll();
        try{
            String username = user.getEmailID();
            String password = user.getPassword();

            if(username.equals(null) || password.equals(null)){
                System.out.println("Username or password is null");
            }
        }catch (NullPointerException e){
            LOG.error("Bad request");
            return new ResponseEntity<String>("{\"message\":\"Enter username and password\"}", responseHeaders, HttpStatus.FORBIDDEN);
        }
        for(User user1 : users) {

            if (user.getEmailID().equals(user1.getEmailID())) {
                return new ResponseEntity<String>("{\"message\":\"Account already exits\"}", responseHeaders, HttpStatus.CONFLICT);
            }
        }
        if(isValidEmailAddress(user.getEmailID())){
            if(isValidPassword(user.getPassword(),errorList)) {
                user.setPassword(Password.hashPassword(user.getPassword()));
                userRepository.save(user);
                LOG.info("User created");
                return new ResponseEntity<String>("{\"message\": \"" + "Account created Successfully." + "\"}".toString(), responseHeaders, HttpStatus.OK);
            } else {
                if(!errorList.isEmpty()) {
                 return new ResponseEntity<String>("{\"message\": \"" + errorList.toString() + "\"}",responseHeaders,HttpStatus.BAD_REQUEST);
                }
            }
        } else {
            return new ResponseEntity<String>("{\"message\": \"Invalid Email\"}", responseHeaders, HttpStatus.NOT_ACCEPTABLE);

        }
        return new ResponseEntity<String>("{\"message\": \"BAD Request\"}", responseHeaders, HttpStatus.BAD_GATEWAY);
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
