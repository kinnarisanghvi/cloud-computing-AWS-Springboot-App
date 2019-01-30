package com.csye6225.spring2019.model;


import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "Users")
public class User {
//    email and password
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull
    @Column(name = "emailID",unique = true,nullable = false)
    private String email;

    public User(@NotNull String email, @NotNull String password) {
        this.email = email;
        this.password = password;
    }

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}