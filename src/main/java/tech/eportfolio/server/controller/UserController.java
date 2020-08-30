package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.EmailAlreadyInUseException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    // Aggregate root

    @GetMapping("/")
    public List<User> findAll() {
        return service.findAll();
    }

    @PostMapping("/")
    public User createOneUser(@RequestBody UserDTO userDTO) {
        Optional<User> user = service.findUserByEmail(userDTO.getEmail());
        if (user.isPresent()) {
            throw new EmailAlreadyInUseException(userDTO.getEmail());
        }
        return service.save(service.fromUserDTO(userDTO));
    }

    // Single item

    @GetMapping("/{id}")
    public User findOneUser(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
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
