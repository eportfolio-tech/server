package tech.eportfolio.server.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.UserService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "users")
@Qualifier("UserServiceCacheImpl")
@Primary
public class UserServiceCacheImpl implements UserService, UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    private final UserService userService;

    @Autowired
    public UserServiceCacheImpl(@Qualifier("UserDetailsService") UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean verifyPassword(User user, String password) {
        return userService.verifyPassword(user, password);
    }

    @Override
    public User register(User user, boolean createAvatar) {
        return userService.register(user, createAvatar);
    }

    @Override
    public String createGithubAvatar(User user) {
        return userService.createGithubAvatar(user);
    }

    @Override
    @CachePut(key = "#user.username")
    public User changePassword(User user, String password) {
        return userService.changePassword(user, password);
    }

    @Override
    @CachePut(key = "#user.username")
    public User save(User user) {
        return userService.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userService.findByEmail(email);
    }

    @Override
    public List<User> findByIdIn(List<String> ids) {
        return userService.findByIdIn(ids);
    }


    @Override
    public List<User> findByUsernameIn(List<String> usernames) {
        return userService.findByUsernameIn(usernames);
    }

    @Override
    public User fromUserDTO(UserDTO userDTO) {
        return userService.fromUserDTO(userDTO);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public Optional<User> findByUsername(String username) {
        return userService.findByUsername(username);
    }


    @Override
    @CacheEvict(key = "#user.username")
    public void delete(User user) {
        userService.delete(user);
    }

    /**
     * Return a list of deleted user with a container whose account has been deleted for at least 7 days
     *
     * @param deleteBeforeDate the days since the user has been deleted
     *                         e.g. Assuming current time is Jan 10th,  deleteBeforeDate is 7 days
     *                         a deleted user whose updatedDate is Jan 2nd will be included in the list
     *                         but a deleted user whose updatedDate is Jan 8th won't be included
     */
    @Override
    public List<User> findDeletedUserWithContainer(Date deleteBeforeDate) {
        return userService.findDeletedUserWithContainer(deleteBeforeDate);
    }

    @Override
    public List<User> saveAll(List<User> users) {
        return userService.saveAll(users);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userService.loadUserByUsername(username);
    }

}