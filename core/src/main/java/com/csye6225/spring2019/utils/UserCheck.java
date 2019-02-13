package com.csye6225.spring2019.utils;

import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.UserRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;


public class UserCheck {

    @Autowired
    UserRepository userRepository;
    public String loginUser(HttpServletRequest request, HttpServletResponse response) throws ServletException {


        String header = request.getHeader("Authorization");
        if (header != null && header.contains("Basic")) {
            String userDetails[] = new String[2];
            assert header.substring(0, 6).equals("Basic");
            String basicAuthEncoded = header.substring(6);
            String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
            userDetails = basicAuthAsString.split(":", 2);

            User userExists = userRepository.findByEmail(userDetails[0]);
            String email = userDetails[0];
            String password = userDetails[1];
            System.out.println(email + "  " + password);


            if (userExists == null) {
                throw new ServletException("User email not found.");
            }

            boolean flag = Password.checkPassword(password, userExists.getPassword());
            // long userid = user1.getId();

            if (!flag) {
                throw new ServletException("Invalid login. Please check your name and password.");
            }

            //        "{\"message\":
            return "Success";
        }
        return "failed";
    }
}