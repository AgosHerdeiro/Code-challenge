package com.test.emailservice.delivery;

import com.test.emailservice.domain.mails.*;
import com.test.emailservice.domain.users.User;
import com.test.emailservice.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("")
public class MailController {

    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final MailQuotaServiceImpl mailQuotaServiceImpl;
    @Autowired
    private final SendGridMailServiceImpl sendGridMailServiceImpl;
    @Autowired
    private final SpringMailServiceImpl springMailServiceImpl;

    @Autowired
    public MailController(JwtTokenProvider jwtTokenProvider, MailQuotaServiceImpl mailQuotaServiceImpl,
                          SendGridMailServiceImpl sendGridMailServiceImpl,
                          SpringMailServiceImpl springMailServiceImpl) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailQuotaServiceImpl = mailQuotaServiceImpl;
        this.sendGridMailServiceImpl = sendGridMailServiceImpl;
        this.springMailServiceImpl = springMailServiceImpl;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody RequestData requestData,
                                            HttpServletRequest httpRequest) {

        Mail mailRequest = requestData.getMailRequest();
        User userRequest = requestData.getUserRequest();

        String token = jwtTokenProvider.resolveToken(httpRequest);
        if (token != null && jwtTokenProvider.validateToken(token)) {

            if (LocalDate.now().isAfter(userRequest.getLastSentDate())) {
                mailQuotaServiceImpl.resetEmailsSent(userRequest);
                userRequest.setLastSentDate(LocalDate.now());
            }

            if (!mailQuotaServiceImpl.hasReachedDailyQuota(userRequest)) {

                try {
                    mailQuotaServiceImpl.increaseEmailsSent(userRequest);
                    return sendGridMailServiceImpl.sendEmail(mailRequest);
                } catch (Exception sendGridEmailException) {
                    try {
                        mailQuotaServiceImpl.increaseEmailsSent(userRequest);
                        return springMailServiceImpl.sendEmail(mailRequest);
                    } catch (Exception springMailExcepcion) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error al enviar el email.");
                    }
                }

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Has alcanzado tu cuota diaria de emails.");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no válido o faltante.");
        }
    }

}