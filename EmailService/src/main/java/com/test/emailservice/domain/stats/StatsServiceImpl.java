package com.test.emailservice.domain.stats;

import com.test.emailservice.domain.users.User;
import com.test.emailservice.domain.users.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StatsServiceImpl implements StatsService {

    private static List<User> usersWithEmailsSent;
    private UserServiceImpl userServiceImpl;

    @Autowired
    public StatsServiceImpl(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public List<User> getUsersListWithEmailsSent() {
        List<User> allUsersList = userServiceImpl.findAll();

        for (User user : allUsersList) {
            if (user.getEmailsSent() > 0) {
                usersWithEmailsSent.add(user);
            }
        }

        return allUsersList;
    }

}
