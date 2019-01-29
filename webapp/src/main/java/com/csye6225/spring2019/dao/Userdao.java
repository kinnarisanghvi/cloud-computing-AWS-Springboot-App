package com.csye6225.spring2019.dao;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.csye6225.spring2019.model.User;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class Userdao extends DAO {

    Session session = getSession();

    public User get(String username, String password) {
        try {
            begin();
            Query q = getSession().createQuery("from Users where emailID = :username and password = :password");
            q.setString("username", username);
            q.setString("password", password);
            User user = (User) q.uniqueResult();
            commit();
            return user;
        } catch (HibernateException e) {
            rollback();
            System.out.println("Error in login "+ e.getMessage());
        }
        return null;
    }

    public registerUser(String emailID1, String password1) {
        try {
            begin();

            Query query = session.createQuery("insert into Users(emailID, password)" +
                    "select emailID, password from Users");
            int result = query.executeUpdate();

            commit();
            session.close();

        }catch (HibernateException e) {
            rollback();
            System.out.println("Error in login "+ e.getMessage());
        }

    }

}

