package com.test.emailservice.domain.stats;

import com.test.emailservice.domain.users.User;

import java.util.List;

public interface StatsService {

    List<User> getUsersListWithEmailsSent();
}
