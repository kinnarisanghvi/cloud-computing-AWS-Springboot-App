package com.csye6225.spring2019.controller;


import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.utils.Password;
import org.springframework.http.MediaType;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public String getUserRequest()
    {

        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }

    @POST
    @Path("/user/register")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    public String createUser(@FormParam("name") String name,
                             @FormParam("password") String password,@Context HttpServletResponse servletResponse) throws IOException
    {


        User user = new User(name, password);
//        int result = userDao.addUser(user);
        //if (result == 1) {

        return ("User Succesfully created");

    }




}


