package tech.eportfolio.server;

import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.Exceptions.UserNotFoundException;

import java.util.List;

@RestController
public class UserController {
    private final UserRepository repository;

    UserController(UserRepository repository) {
        this.repository = repository;
    }

    // Aggregate root

    @GetMapping("/users")
    List<User> findall() {
        return (List<User>) repository.findAll();
    }

    @PostMapping("/users")
    User createNewUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    // Single item

    @GetMapping("/users/{id}")
    User findOneUser(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

//        @PutMapping("/users/{id}")
//        User replaceEmployee(@RequestBody User newUser, @PathVariable Long id) {
//
//            return repository.findById(id)
//                    .map(user -> {
//                        user.setFirstName(newUser.getFirstName());
//                        user.setFirstName(newUser.getFirstName());
//                        return repository.save(user);
//                    })
//                    .orElseGet(() -> {
//                        newUser.setId(id);
//                        return repository.save(newUser);
//                    });
//        }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
