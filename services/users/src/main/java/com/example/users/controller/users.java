package com.example.users.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/users")
public class users {

    private static final Logger log = LoggerFactory.getLogger(users.class);

    @GetMapping(path = "/")
    public  String HomePage()
    {
       log.info("Inside Homepage");
        return "Hello from homepage";
    }

}
