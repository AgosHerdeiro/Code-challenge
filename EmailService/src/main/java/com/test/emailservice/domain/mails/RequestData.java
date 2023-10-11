package com.test.emailservice.domain.mails;

import com.test.emailservice.domain.users.User;

public class RequestData {

    private Mail mailRequest;
    private User userRequest;

    public RequestData(Mail mailRequest, User userRequest) {
        this.mailRequest = mailRequest;
        this.userRequest = userRequest;
    }

    public Mail getMailRequest() {
        return mailRequest;
    }
    public void setMailRequest(Mail mailRequest) {
        this.mailRequest = mailRequest;
    }
    public User getUserRequest() {
        return userRequest;
    }
    public void setUserRequest(User userRequest) {
        this.userRequest = userRequest;
    }

}
