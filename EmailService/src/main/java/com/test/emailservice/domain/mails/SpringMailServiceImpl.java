package com.test.emailservice.domain.mails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpringMailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public SpringMailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public ResponseEntity<String> sendEmail(Mail mailRequest) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(mailRequest.getTo());
        mail.setSubject(mailRequest.getSubject());
        mail.setText(mailRequest.getBody());
        mail.setFrom(mailRequest.getFrom());

        try {
            javaMailSender.send(mail);
            return ResponseEntity.status(HttpStatus.OK).body("Email enviado correctamente.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el email.");
        }
    }


}
