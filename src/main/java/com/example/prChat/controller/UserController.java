package com.example.prChat.controller;

import com.example.prChat.model.User;
import com.example.prChat.repo.UserRepo;
import com.example.prChat.services.JwtUtil;
import com.example.prChat.services.UserSignup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepo repo;

    @Autowired
    private UserSignup userSignup;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {

        Optional<User> exists = repo.findByEmail(user.getEmail());

        if (!exists.isPresent()) {

            User x = userSignup.registerNewUser(user);

            if (x.getEmail().equals(user.getEmail())) {

                return x.get_id();

            }

        }

        return "Signup failed";

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginInsecure(@RequestBody Map<String, String> payload) {

        String email = payload.get("email");

        Optional<User> exists = repo.findByEmail(email);

        if (exists.isPresent()) {
            User t = exists.get();

            // In a secure system, you would verify a password or code here.
            // This implementation skips verification entirely.

            // Build a UserDetails object for token generation
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    t.get_id(), // Using _id as the username for this example
                    "", // No password
                    Collections.singletonList(new SimpleGrantedAuthority("USER")) // Or get role from user object
            );

            // Generate the token
            String token = jwtUtil.generateToken(userDetails, t);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("token", token);
            return ResponseEntity.ok(responseBody);
        } else {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("error", "invalid email");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        }


    }
}
