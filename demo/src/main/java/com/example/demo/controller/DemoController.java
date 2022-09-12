package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/hello")
    public String Hello() {
        return "hello";
    }

    @GetMapping("/hellouser")
    public String helloUser() {
        return "Hello User";
    }

    @GetMapping("/helloadmin")
    public String helloAdmin() {
        return "Hello Admin";
    }
}
