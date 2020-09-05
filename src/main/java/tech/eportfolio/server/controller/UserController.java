package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.dto.PasswordResetRequestBody;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserTag;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.security.SecurityConstant;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.UserTagService;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

/**
 * @author Haswell
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final UserService userService;

    private final UserRepository repository;

    private final UserTagService userTagService;

    @Autowired
    public UserController(UserService service, UserRepository repository, UserTagService userTagService) {
        this.userService = service;
        this.repository = repository;
        this.userTagService = userTagService;
    }

    /**
     * Find a user by username
     *
     * @param username username
     * @return User
     */
    @GetMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public User findOneUser(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
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
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<Null> passwordReset(@PathVariable String username, @RequestBody @Valid PasswordResetRequestBody passwordResetRequestBody) throws AccessDeniedException {
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        User user = userOptional.get();
        if (userService.verifyPassword(user, passwordResetRequestBody.getOldPassword())) {
            userService.changePassword(user, passwordResetRequestBody.getNewPassword());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException(SecurityConstant.ACCESS_DENIED_MESSAGE);
        }
    }

    @PutMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
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


    /**
     * Return user's tags
     *
     * @param username username
     * @return User
     */
    @GetMapping("/{username}/tags")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public List<Tag> getUserTags(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        return userTagService.findTagsByUsername(username);
    }

    /**
     * Return user's tags
     *
     * @param username username
     * @return User
     */
    @PostMapping("/{username}/tags")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public List<UserTag> addUserTag(@PathVariable String username, @RequestBody List<Tag> tags) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        return userTagService.batchAssign(user.get(), tags);
    }

    @DeleteMapping("/{username}/tags")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public List<UserTag> deleteUserTags(@PathVariable String username, @RequestBody List<Tag> tags) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        return userTagService.delete(user.get(), tags);
    }
}
