package com.test.emailservice.delivery;

import com.test.emailservice.domain.stats.StatsServiceImpl;
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

import java.util.List;

@RestController
@RequestMapping("")
public class StatsController {

    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final StatsServiceImpl statsServiceImpl;

    @Autowired
    public StatsController(JwtTokenProvider jwtTokenProvider, StatsServiceImpl statsServiceImpl) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.statsServiceImpl = statsServiceImpl;
    }

    @PostMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestBody User userRequest, HttpServletRequest httpRequest) {
        String token = jwtTokenProvider.resolveToken(httpRequest);

        if (token != null && jwtTokenProvider.validateToken(token)) {

            if (userRequest.getRole().equals("ADMIN")) {
                List<User> userList = statsServiceImpl.getUsersListWithEmailsSent();

                if (userList == null || userList.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("No se encontraron usuarios.");
                }
                return ResponseEntity.status(HttpStatus.OK).body(userList);

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permiso para acceder a este recurso.");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no válido o faltante.");
        }

    }

}
