package com.test.emailservice.domain.mails;

import com.test.emailservice.domain.users.User;

public interface MailQuotaService {

    void increaseEmailsSent(User user);

    boolean hasReachedDailyQuota(User user);

    void resetEmailsSent(User user);
}
