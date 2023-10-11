package com.test.emailservice.domain.mails;

import com.test.emailservice.domain.users.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MailQuotaServiceImpl implements MailQuotaService {

    @Override
    public void increaseEmailsSent(User user) {
        user.setEmailsSent(user.getEmailsSent() + 1);
    }

    @Override
    public boolean hasReachedDailyQuota(User user) {
        return !(user.getEmailsSent() < user.getDailyQuota());
    }

    @Override
    public void resetEmailsSent(User user) {
        user.setEmailsSent(0);
    }

}
