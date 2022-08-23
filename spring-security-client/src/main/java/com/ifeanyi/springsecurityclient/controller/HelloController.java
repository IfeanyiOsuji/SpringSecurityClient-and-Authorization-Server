package com.ifeanyi.springsecurityclient.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello(){
        return "Welcome to Ifeanyi's spring security demo";
    }


}
