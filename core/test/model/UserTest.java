package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.csye6225.spring2019.model.User;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void checkUserPOJO() {
        User user = new User(); // User is tested
        user.setPassword("passwordTest123!");
        user.setEmailID("test.user@test.com");
        assertEquals("test.user@test.com", user.getEmailID(), "email must be test.user@test.com");
        assertEquals("passwordTest123!", user.getPassword(), "password must be passwordTest123!");
    }
}

