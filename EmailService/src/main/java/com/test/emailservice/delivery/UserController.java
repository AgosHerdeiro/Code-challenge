package com.test.emailservice.delivery;

import com.test.emailservice.domain.users.User;
import com.test.emailservice.domain.users.UserServiceImpl;
import com.test.emailservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class UserController {

    @Autowired
    private final UserServiceImpl userServiceImpl;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl, BCryptPasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider) {
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/user-register")
    public ResponseEntity<String> registerUser(@RequestBody User userRequest) {
        String username = userRequest.getUsername();
        String password = userRequest.getPassword();
        String role = userRequest.getRole();

        User existingUser = userServiceImpl.getUserByUsername(username);
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Usuario ya existente.");
        } else {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setRole(role != null ? role : "USER");
            userServiceImpl.saveUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuario registrado exitosamente.");
        }
    }

    @PostMapping("/validate-login")
    public ResponseEntity<String> loginUser(@RequestBody User userRequest) {
        String username = userRequest.getUsername();
        String password = userRequest.getPassword();
        String passwordEncoded = passwordEncoder.encode(password);

        User existingUser = userServiceImpl.verifyCredentials(username, passwordEncoded);

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales incorrectas.");
        } else {
            String token = jwtTokenProvider.generateToken(username);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(token);
        }
    }
}
