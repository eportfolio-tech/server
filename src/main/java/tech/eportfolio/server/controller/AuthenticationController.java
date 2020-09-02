package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.EmailAlreadyInUseException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.UserService;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Random;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/authentication")
public class AuthenticationController {

    private final Random random = new Random(System.currentTimeMillis());
    @Autowired
    private UserService service;
    @Autowired
    private UserRepository repository;

    @PostMapping("/signup")
    public User signUp(@RequestBody @Valid UserDTO userDTO) {
        Optional<User> user = service.findUserByEmail(userDTO.getEmail());
        if (user.isPresent()) {
            throw new EmailAlreadyInUseException(userDTO.getEmail());
        }
        if (StringUtils.isEmpty(userDTO.getUsername())) {
            userDTO.setUsername(userDTO.getFirstName() + userDTO.getLastName() + random.nextInt());
        }
        return service.save(service.fromUserDTO(userDTO));
    }

}
