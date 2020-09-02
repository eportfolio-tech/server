package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.dto.PasswordResetRequestBody;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.security.SecurityConstant;
import tech.eportfolio.server.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

/**
 * @author Haswell
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    private final UserRepository repository;

    @Autowired
    public UserController(UserService service, UserRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    //    @PostMapping("/")
//    public User createOneUser(@RequestBody @Valid UserDTO userDTO) {
//        Optional<User> user = service.findUserByEmail(userDTO.getEmail());
//        if (user.isPresent()) {
//            throw new EmailAlreadyInUseException(userDTO.getEmail());
//        }
//        if (StringUtils.isEmpty(userDTO.getUsername())) {
//            userDTO.setUsername(userDTO.getFirstName() + userDTO.getLastName() + random.nextInt());
//        }
//        return service.save(service.fromUserDTO(userDTO));
//    }

    /**
     * Find a user by username
     *
     * @param username username
     * @return User
     */
    @GetMapping("/{username}")
    public User findOneUser(@PathVariable String username) {
        Optional<User> user = service.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        return user.get();
    }

    /**
     * Reset password of an given user
     *
     * @param username username
     * @return Nothing
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/{username}/password-reset")
    public ResponseEntity<Null> passwordReset(@PathVariable String username, @RequestBody @Valid PasswordResetRequestBody passwordResetRequestBody) throws AccessDeniedException {
        Optional<User> userOptional = service.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        User user = userOptional.get();
        if (service.verifyPassword(user, passwordResetRequestBody.getOldPassword())) {
            service.changePassword(user, passwordResetRequestBody.getNewPassword());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException(SecurityConstant.ACCESS_DENIED_MESSAGE);
        }
    }

//    @PutMapping("/{username}")
//    public User updateOneUser(@RequestBody User changedUser, @PathVariable Long id) {
//        return repository.findById(id)
//                .map(user -> {
//                    user.setFirstName(changedUser.getFirstName());
//                    user.setLastName(changedUser.getLastName());
//                    return repository.save(user);
//                })
//                .orElseGet(() -> {
//                    changedUser.setId(id);
//                    return repository.save(changedUser);
//                });
//    }
}
