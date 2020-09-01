package tech.eportfolio.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetaController {

    @RequestMapping("/secure")
    public String index() {
        return "Greetings from a secure endpoint";
    }
}
