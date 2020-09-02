package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.handler.AuthenticationExceptionHandler;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.UserService;

import javax.validation.Valid;
import java.util.Random;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/authentication")
public class AuthenticationController extends AuthenticationExceptionHandler {

    private final Random random = new Random(System.currentTimeMillis());
    @Autowired
    private UserService service;
    @Autowired
    private UserRepository repository;

    @PostMapping("/signup")
    public User signUp(@RequestBody @Valid UserDTO userDTO) {
        return service.register(service.fromUserDTO(userDTO));
    }

}
