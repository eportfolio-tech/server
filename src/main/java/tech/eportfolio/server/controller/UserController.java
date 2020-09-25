package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.PasswordResetException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.common.utility.NullAwareBeanUtilsBean;
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
        return new SuccessResponse<>("user", user).toOk();
    }


    /**
     * Patch user's profile
     *
     * @param username             username to update
     * @param userPatchRequestBody new user information
     * @return updated user
     */
    /*
     */
    @PatchMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<User>> updateUser(@Valid @PathVariable String username,
                                                            @RequestBody @Valid UserPatchRequestBody userPatchRequestBody) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        NullAwareBeanUtilsBean.copyProperties(userPatchRequestBody, user);
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
    public ResponseEntity<SuccessResponse<Object>> passwordReset(@PathVariable String username, @Valid @RequestBody PasswordResetRequestBody passwordResetRequestBody) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        if (!userService.verifyPassword(user, passwordResetRequestBody.getOldPassword())) {
            throw new PasswordResetException(user.getUsername(), "Old password is incorrect");
        }
        userService.changePassword(user, passwordResetRequestBody.getNewPassword());
        return new SuccessResponse<>().toAccepted();
    }

    /**
     * Return user's tags
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
