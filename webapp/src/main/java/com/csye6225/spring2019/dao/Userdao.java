package com.csye6225.spring2019.dao;
import com.csye6225.spring2019.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class Userdao extends DAO {

    Session session = getSession();

    public User get(String username, String password) {
        try {
            begin();
            Query q = getSession().createQuery("from User where email = :username and password = :password");
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

    public int registerUser(User user) {
        int result=0;
        try {
            begin();

            String query = "insert into Users values("+user.getEmail()+","+user.getPassword()+")";
            Query q = session.createQuery(query);
            result = q.executeUpdate();
            System.out.println("Chal gaya User added "+result);
            commit();
            session.close();

        }catch (HibernateException e) {
            rollback();
            System.out.println("Error in login "+ e.getMessage());
        }


        return result;
    }

}

