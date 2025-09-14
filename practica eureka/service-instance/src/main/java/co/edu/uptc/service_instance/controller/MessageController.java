package co.edu.uptc.service_instance.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    private Environment environment;

    @GetMapping("/message")
    public String getMessage() {
        return "Hello! Eureka client from PORT number : " 
                + environment.getProperty("local.server.port");
    }
}
