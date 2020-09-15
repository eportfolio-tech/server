package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.dto.PasswordResetRequestBody;
import tech.eportfolio.server.dto.UserPatchRequestBody;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserTag;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.UserTagService;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Haswell
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final UserService userService;

    private final UserTagService userTagService;

    @Autowired
    public UserController(UserService service, UserTagService userTagService) {
        this.userService = service;
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
    public ResponseEntity<SuccessResponse<User>> findOneUser(@PathVariable String username) {
        User user = userService.findByUsername(username).
                orElseThrow(() -> (new UserNotFoundException(username)));
        return new SuccessResponse<User>("user", user).toOk();
    }


    /**
     * Patch user's profile
     *
     * @param username             username to update
     * @param userPatchRequestBody new user information
     * @return updated user
     */
    @PatchMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<User>> updateUser(@Valid @PathVariable String username,
                                                            @RequestBody UserPatchRequestBody userPatchRequestBody) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if (userPatchRequestBody.getFirstName() != null) {
            user.setFirstName(userPatchRequestBody.getFirstName());
        }
        if (userPatchRequestBody.getLastName() != null) {
            user.setLastName(userPatchRequestBody.getLastName());
        }
        if (userPatchRequestBody.getPhone() != null) {
            user.setPhone(userPatchRequestBody.getPhone());
        }

        if (userPatchRequestBody.getEmail() != null) {
            user.setEmail(userPatchRequestBody.getEmail());
        }
        if (userPatchRequestBody.getTitle() != null) {
            user.setTitle(userPatchRequestBody.getTitle());
        }

        user = userService.save(user);

        return new SuccessResponse<>("user", user).toOk();
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
    public ResponseEntity<SuccessResponse<Object>> passwordReset(@PathVariable String username, @Valid @RequestBody PasswordResetRequestBody passwordResetRequestBody) throws AccessDeniedException {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        if (!userService.verifyPassword(user, passwordResetRequestBody.getOldPassword())) {
            /*
            TODO: create a new exception
             */
            throw new RuntimeException("Old password is incorrect");
        }
        userService.changePassword(user, passwordResetRequestBody.getNewPassword());
        return new SuccessResponse<>().toAccepted();
    }

//    @PutMapping("/{username}")
//    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
//    public User updateOneUser(@RequestBody User changedUser, @PathVariable String username) {
//        return repository.findgBy(id)
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

    /**
     * Return user's tags
     *
     * @param username username
     * @return User
     */
    @GetMapping("/{username}/tags")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<Tag>>> getUserTags(@PathVariable String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> (new UserNotFoundException(username)));
        List<Tag> tags = userTagService.findTagsByUser(user);
        return new SuccessResponse<>("tag", tags).toOk();
    }

    /**
     * Return user's tags
     *
     * @param username username
     * @return User
     */
    @PostMapping("/{username}/tags")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<UserTag>>> addUserTag(@PathVariable String username, @RequestBody List<Tag> tags) {
        User user = userService.findByUsername(username).orElseThrow(() -> (new UserNotFoundException(username)));
        return new SuccessResponse<>("tag", userTagService.batchAssign(user, tags)).toOk();
    }

    @PostMapping("/{username}/deleteTags")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<UserTag>>> deleteUserTags(@PathVariable String username, @RequestBody List<Tag> tags) {
        User user = userService.findByUsername(username).orElseThrow(() -> (new UserNotFoundException(username)));
        return new SuccessResponse<>("tag", userTagService.delete(user, tags)).toOk();
    }
}
