package com.test.emailservice.domain.users;

import com.test.emailservice.infraestructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(User newUser) {
        userRepository.saveUser(newUser);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    @Override
    public User verifyCredentials(String username, String password) {
        return userRepository.verifyCredentials(username, password);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
