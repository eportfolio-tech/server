package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.EmailAlreadyInUseException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.UserService;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    private final Random random = new Random(System.currentTimeMillis());

    @PostMapping("/")
    public User createOneUser(@RequestBody @Valid UserDTO userDTO) {
        Optional<User> user = service.findUserByEmail(userDTO.getEmail());
        if (user.isPresent()) {
            throw new EmailAlreadyInUseException(userDTO.getEmail());
        }
        if (StringUtils.isEmpty(userDTO.getUsername())) {
            userDTO.setUsername(userDTO.getFirstName() + userDTO.getLastName() + random.nextInt());
        }
        return service.save(service.fromUserDTO(userDTO));
    }

    // Single item
    @GetMapping("/{id}")
    public User findOneUser(@PathVariable Long id) {
        Optional<User> user = service.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        return user.get();
    }

    @PutMapping("/{id}")
    public User updateOneUser(@RequestBody User changedUser, @PathVariable Long id) {
        return repository.findById(id)
                .map(user -> {
                    user.setFirstName(changedUser.getFirstName());
                    user.setLastName(changedUser.getLastName());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    changedUser.setId(id);
                    return repository.save(changedUser);
                });
    }
}
