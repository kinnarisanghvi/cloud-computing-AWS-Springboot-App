package com.csye6225.spring2019.utils;

import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.UserRepository;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;


public class UserCheck {


    public String loginUser(HttpServletRequest request, HttpServletResponse response, UserRepository userRepository) {

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

            if(email.equals(null) || password.equals(null)){
                return "4";
            }
            if (userExists == null) {
                return "0"; //not exists
            }

            boolean flag = Password.checkPassword(password, userExists.getPassword());


            if (!flag) {
                return "1"; // invalid login
            }

            return "Success"+","+userExists.getId();
        }
        return "2";
    }
}