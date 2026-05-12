package com.gateway.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/orders")
    public String getUsers() {
        return "Response from Order Service";
    }
}