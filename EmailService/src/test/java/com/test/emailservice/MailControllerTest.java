package com.test.emailservice;


import com.test.emailservice.delivery.MailController;
import com.test.emailservice.domain.mails.*;
import com.test.emailservice.domain.users.User;
import com.test.emailservice.security.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MailControllerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private MailQuotaServiceImpl mailQuotaServiceImpl;
    @Mock
    private SendGridMailServiceImpl sendGridMailServiceImpl;
    @Mock
    private SpringMailServiceImpl springMailServiceImpl;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MailController mailController;

    @Test
    void givenInvalidTokenWhenSendingEmailsThenReturnUnauthorized() {
        String username = "username";
        String password = "password";
        String role = "ADMIN";

        User userRequest = new User();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole(role);

        String from = "from";
        String to = "to";
        String subject = "subject";
        String body = "body";
        Mail mailRequest = new Mail(from, to, subject, body);
        mailRequest.setFrom(from);
        mailRequest.setTo(to);
        mailRequest.setSubject(subject);
        mailRequest.setBody(body);

        RequestData requestData = new RequestData(mailRequest, userRequest);

        String tokenExpected = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secret_Key")
                .compact();

        when(jwtTokenProvider.resolveToken(request)).thenReturn(tokenExpected);
        when(jwtTokenProvider.validateToken(tokenExpected)).thenReturn(false);

        ResponseEntity<String> response = mailController.sendEmail(requestData, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void givenValidTokenWhenSendingEmailsButHasReachedDailyQuotaThenReturnForbidden() {
        String username = "username";
        String password = "password";
        String role = "ADMIN";
        LocalDate lastSenDate = LocalDate.now();
        int emailsSent = 1000;
        User userRequest = new User();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole(role);
        userRequest.setLastSentDate(lastSenDate);
        userRequest.setEmailsSent(emailsSent);

        String from = "from";
        String to = "to";
        String subject = "subject";
        String body = "body";
        Mail mailRequest = new Mail(from, to, subject, body);
        mailRequest.setFrom(from);
        mailRequest.setTo(to);
        mailRequest.setSubject(subject);
        mailRequest.setBody(body);

        RequestData requestData = new RequestData(mailRequest, userRequest);

        String tokenExpected = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secret_Key")
                .compact();

        when(jwtTokenProvider.resolveToken(request)).thenReturn(tokenExpected);
        when(jwtTokenProvider.validateToken(tokenExpected)).thenReturn(true);
        when(mailQuotaServiceImpl.hasReachedDailyQuota(userRequest)).thenReturn(true);

        ResponseEntity<String> response = mailController.sendEmail(requestData, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void givenValidTokenWhenSendingEmailsAndHasNoReachedDailyQuotaThenReturnSuccessEmailSendWithSendGrid() {
        String username = "username";
        String password = "password";
        String role = "ADMIN";
        LocalDate lastSenDate = LocalDate.now();
        int emailsSent = 1000;
        User userRequest = new User();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole(role);
        userRequest.setLastSentDate(lastSenDate);
        userRequest.setEmailsSent(emailsSent);

        String from = "from";
        String to = "to";
        String subject = "subject";
        String body = "body";
        Mail mailRequest = new Mail(from, to, subject, body);
        mailRequest.setFrom(from);
        mailRequest.setTo(to);
        mailRequest.setSubject(subject);
        mailRequest.setBody(body);

        RequestData requestData = new RequestData(mailRequest, userRequest);

        String tokenExpected = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secret_Key")
                .compact();

        when(jwtTokenProvider.resolveToken(request)).thenReturn(tokenExpected);
        when(jwtTokenProvider.validateToken(tokenExpected)).thenReturn(true);
        when(mailQuotaServiceImpl.hasReachedDailyQuota(userRequest)).thenReturn(false);

        ResponseEntity<String> responseEntity = ResponseEntity.status(HttpStatus.OK)
                .body("Email enviado correctamente,");

        when(sendGridMailServiceImpl.sendEmail(mailRequest)).thenReturn(responseEntity);

        ResponseEntity<String> response = mailController.sendEmail(requestData, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenValidTokenWhenSendingEmailsAndHasNoReachedDailyQuotaButSendGridFailsThenReturnSuccessEmailSendWithSpringMail() {
        String username = "username";
        String password = "password";
        String role = "ADMIN";
        LocalDate lastSenDate = LocalDate.now();
        int emailsSent = 1000;
        User userRequest = new User();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole(role);
        userRequest.setLastSentDate(lastSenDate);
        userRequest.setEmailsSent(emailsSent);

        String from = "from";
        String to = "to";
        String subject = "subject";
        String body = "body";
        Mail mailRequest = new Mail(from, to, subject, body);
        mailRequest.setFrom(from);
        mailRequest.setTo(to);
        mailRequest.setSubject(subject);
        mailRequest.setBody(body);

        RequestData requestData = new RequestData(mailRequest, userRequest);

        String tokenExpected = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secret_Key")
                .compact();

        when(jwtTokenProvider.resolveToken(request)).thenReturn(tokenExpected);
        when(jwtTokenProvider.validateToken(tokenExpected)).thenReturn(true);
        when(mailQuotaServiceImpl.hasReachedDailyQuota(userRequest)).thenReturn(false);
        when(sendGridMailServiceImpl.sendEmail(any())).thenThrow(new RuntimeException("Error al enviar el email."));

        ResponseEntity<String> responseEntity = ResponseEntity.status(HttpStatus.OK)
                .body("Email enviado correctamente.");
        when(springMailServiceImpl.sendEmail(mailRequest)).thenReturn(responseEntity);
        ResponseEntity<String> response = mailController.sendEmail(requestData, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenValidTokenWhenSendingEmailsAndHasNoReachedDailyQuotaButNeitherCaseWorksThenReturnInternaServerError() {
        String username = "username";
        String password = "password";
        String role = "ADMIN";
        LocalDate lastSenDate = LocalDate.now();
        int emailsSent = 1000;
        User userRequest = new User();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole(role);
        userRequest.setLastSentDate(lastSenDate);
        userRequest.setEmailsSent(emailsSent);

        String from = "from";
        String to = "to";
        String subject = "subject";
        String body = "body";
        Mail mailRequest = new Mail(from, to, subject, body);
        mailRequest.setFrom(from);
        mailRequest.setTo(to);
        mailRequest.setSubject(subject);
        mailRequest.setBody(body);

        RequestData requestData = new RequestData(mailRequest, userRequest);

        String tokenExpected = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secret_Key")
                .compact();

        when(jwtTokenProvider.resolveToken(request)).thenReturn(tokenExpected);
        when(jwtTokenProvider.validateToken(tokenExpected)).thenReturn(true);
        when(mailQuotaServiceImpl.hasReachedDailyQuota(userRequest)).thenReturn(false);
        when(sendGridMailServiceImpl.sendEmail(any())).thenThrow(new RuntimeException("Error al enviar el email."));
        when(springMailServiceImpl.sendEmail(mailRequest)).thenThrow(new RuntimeException("Error al enviar el email."));

        ResponseEntity<String> response = mailController.sendEmail(requestData, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
