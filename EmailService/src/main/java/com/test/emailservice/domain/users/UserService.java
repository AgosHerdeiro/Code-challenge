package com.test.emailservice.domain.users;

import java.util.List;

public interface UserService {

    void saveUser(User newUser);

    User getUserByUsername(String username);

    User verifyCredentials(String username, String password);

    List<User> findAll();
}
