package com.csye6225.spring2019.repository;


import com.csye6225.spring2019.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public default User findByEmail(String email) {
        List<User> users = this.findAll();
        User foundUser = null;
        for (User user1 : users) {
            if (user1.getEmailID().equals(email)) {
                foundUser = user1;
            }
        }
        return foundUser;
    }
}
