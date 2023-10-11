package com.test.emailservice.domain.mails;

import org.springframework.http.ResponseEntity;

public interface MailService {

    ResponseEntity<String> sendEmail(Mail mailRequest);

}
