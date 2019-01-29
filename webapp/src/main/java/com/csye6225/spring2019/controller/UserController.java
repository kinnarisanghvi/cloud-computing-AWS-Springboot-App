package com.csye6225.spring2019.controller;


import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")

public class UserController {
//    Endpoints GET & POST

    @GetMapping("/")
    public void getUserRequest()
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
    }

    @PostMapping("/user/register")
     public void createUser()
    {

        System.out.print("User Succesfully created");
    }





}


