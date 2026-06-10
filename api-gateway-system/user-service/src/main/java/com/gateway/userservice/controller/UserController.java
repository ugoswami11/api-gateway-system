package com.gateway.userservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private int counter = 0;

    @Value("${server.port}")
    private String port;

    @GetMapping("/users")
    public ResponseEntity<String> users() throws Exception {

////        Thread.sleep(7000);
//
//        counter++;
//
//        if (counter < 3) {
//            throw new RuntimeException("Temporary failure");
//        }
//
//        return ResponseEntity.ok("Success after retry");
//
////        return ResponseEntity.ok("Users fetched");

        return ResponseEntity.ok(
                "Response from USER-SERVICE running on port: " + port);

    }
}