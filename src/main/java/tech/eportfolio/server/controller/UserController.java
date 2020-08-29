package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    // Aggregate root

    @GetMapping("/users")
    public List<User> findAll() {
        return service.findAll();
    }

    @PostMapping("/users")
    public User createNewUser(@RequestBody User user) {
        return service.save(user);
    }

    // Single item

    @GetMapping("/users/{id}")
    public User findOneUser(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PutMapping("/users/{id}")
    public User replaceEmployee(@RequestBody User newUser, @PathVariable Long id) {
        return repository.findById(id)
                .map(user -> {
                    user.setFirstName(newUser.getFirstName());
                    user.setLastName(newUser.getLastName());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
