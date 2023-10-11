package com.test.emailservice.domain.mails;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.test.emailservice.config.SendGridConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional
public class SendGridMailServiceImpl implements MailService {

    private final SendGrid sendGrid;
    @Autowired
    private SendGridConfig sendGridConfig;

    @Autowired
    public SendGridMailServiceImpl(SendGridConfig sendGridConfig) {
        this.sendGrid = sendGridConfig.getSendGridApiKey();
    }

    @Override
    public ResponseEntity<String> sendEmail(Mail mailRequest) {
        Email from = new Email(mailRequest.getFrom());
        String subject = mailRequest.getSubject();
        Email to = new Email(mailRequest.getTo());
        Content content = new Content("text/plain", mailRequest.getBody());
        com.sendgrid.helpers.mail.Mail mail = new com.sendgrid.helpers.mail.Mail(from, subject, to, content);

        SendGrid sg = sendGridConfig.getSendGridApiKey();
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() == 202) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Email enviado correctamente.");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el email.");
        }
    }

}