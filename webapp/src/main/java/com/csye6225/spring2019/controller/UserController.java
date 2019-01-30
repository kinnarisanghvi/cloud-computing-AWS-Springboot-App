package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.dao.Userdao;
import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.utils.Password;
import org.springframework.http.MediaType;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.UserDataHandler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@RequestMapping("/note")

public class UserController {
//    Endpoints GET & POST

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public String getUserRequest(@FormParam("name") String name,
                                 @FormParam("password") String password,@Context HttpServletResponse servletResponse)
    {

        Userdao udao = new Userdao();

        User user=(User) udao.get(name,password);
        boolean check_hash_password = Password.checkPassword(password,user.getPassword());
        if(user!=null && !check_hash_password) {
            if(!(user.getPassword().equalsIgnoreCase(password))
                    || !user.getEmail().equalsIgnoreCase(name)){
                return ("Invalid credentials");
            }
            DateFormat dateFormat;
            dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            return "{date: "+dateFormat.format(date)+"}";
        }
        else{
            return ("user does not exist please provide correct credentials");
        }
    }

    @POST
    @Path("/user/register")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    public String createUser(@FormParam("name") String name,
                             @FormParam("password") String password,@Context HttpServletResponse servletResponse) throws IOException
    {

        String hashed_password = Password.hashPassword(password);
        User user = new User(name, hashed_password);
        Userdao userdao = new Userdao();
        userdao.registerUser(user);
        return "{message: 'User successfully created', email: '"+name+"'}";
//        return ("User Succesfully created");

    }




}


