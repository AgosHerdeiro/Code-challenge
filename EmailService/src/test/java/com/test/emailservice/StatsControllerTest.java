package com.test.emailservice;

import com.test.emailservice.delivery.StatsController;
import com.test.emailservice.domain.stats.StatsServiceImpl;
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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsControllerTest {

    @Mock
    private StatsServiceImpl statsServiceImpl;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private StatsController statsController;

    @Test
    void givenInvalidTokenWhenListingStatsThenReturnUnauthorized() {
        String username = "username";
        String password = "password";
        String role = "ADMIN";

        User userRequest = new User();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole(role);

        String tokenExpected = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secret_Key")
                .compact();

        when(jwtTokenProvider.resolveToken(request)).thenReturn(tokenExpected);
        when(jwtTokenProvider.validateToken(tokenExpected)).thenReturn(false);

        ResponseEntity<Object> response = statsController.getStats(userRequest, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void givenValidTokenAndValidRoleWhenListingStatsThenReturnSuccess() {
        String username = "username";
        String password = "password";
        String role = "ADMIN";

        User userRequest = new User();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole(role);

        String username2 = "username";
        String password2 = "password";
        String role2 = "USER";
        int emailsSent = 1;

        User userRequest2 = new User();
        userRequest2.setUsername(username2);
        userRequest2.setPassword(password2);
        userRequest2.setRole(role2);
        userRequest2.setEmailsSent(emailsSent);

        List<User> userList = statsServiceImpl.getUsersListWithEmailsSent();
        userList.add(userRequest2);

        String tokenExpected = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secret_Key")
                .compact();

        when(jwtTokenProvider.resolveToken(request)).thenReturn(tokenExpected);
        when(jwtTokenProvider.validateToken(tokenExpected)).thenReturn(true);
        when(statsServiceImpl.getUsersListWithEmailsSent()).thenReturn(userList);

        ResponseEntity<Object> response = statsController.getStats(userRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenValidTokenAndValidRoleWhenListingStatsButNoUsersFoundThenReturnNotFound() {
        String username = "username";
        String password = "password";
        String role = "ADMIN";

        User userRequest = new User();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole(role);

        String tokenExpected = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secret_Key")
                .compact();

        when(jwtTokenProvider.resolveToken(request)).thenReturn(tokenExpected);
        when(jwtTokenProvider.validateToken(tokenExpected)).thenReturn(true);
        when(statsServiceImpl.getUsersListWithEmailsSent()).thenReturn(null);

        ResponseEntity<Object> response = statsController.getStats(userRequest, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void givenValidTokenButInvalidRoleWhenListingStatsThenReturnFailure() {
        String username = "username";
        String password = "password";
        String role = "USER";

        User userRequest = new User();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole(role);

        String tokenExpected = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "secret_Key")
                .compact();

        when(jwtTokenProvider.resolveToken(request)).thenReturn(tokenExpected);
        when(jwtTokenProvider.validateToken(tokenExpected)).thenReturn(true);

        ResponseEntity<Object> response = statsController.getStats(userRequest, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}
