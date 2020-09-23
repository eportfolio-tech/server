package tech.eportfolio.server.service.impl;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.constant.Role;
import tech.eportfolio.server.common.exception.EmailExistException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.exception.UsernameExistException;
import tech.eportfolio.server.common.utility.AvatarGenerator;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.AzureStorageService;
import tech.eportfolio.server.service.UserService;

import java.io.InputStream;
import java.util.*;

@Service
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Random random = new Random(System.currentTimeMillis());

    private BoundMapperFacade<UserDTO, User> boundMapper;

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private AzureStorageService azureStorageService;

    private AvatarGenerator avatarGenerator;


    @Autowired
    public void setAzureStorageService(AzureStorageService azureStorageService) {
        this.azureStorageService = azureStorageService;
    }

    @Autowired
    public void setAvatarGenerator(AvatarGenerator avatarGenerator) {
        this.avatarGenerator = avatarGenerator;
    }


    @Autowired
    public void setBoundMapper(BoundMapperFacade<UserDTO, User> boundMapper) {
        this.boundMapper = boundMapper;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setBcryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    public BoundMapperFacade<UserDTO, User> boundMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        return mapperFactory.getMapperFacade(UserDTO.class, User.class);
    }

    @Override
    public boolean verifyPassword(User user, String password) {
        return bCryptPasswordEncoder.matches(password, user.getPassword());
    }

    @Override
    public User register(User user) {
        Optional<User> emailResult = findByEmail(user.getEmail());
        if (emailResult.isPresent()) {
            throw new EmailExistException(user.getEmail());
        }

        if (StringUtils.isEmpty(user.getUsername())) {
            user.setUsername(user.getFirstName() + user.getLastName() + random.nextInt());
        }

        Optional<User> userNameResult = findByUsername(user.getUsername());
        if (userNameResult.isPresent()) {
            throw new UsernameExistException(user.getUsername());
        }
        user.setPassword(encodePassword(user.getPassword()));
        user.setRoles(Role.ROLE_UNVERIFIED_USER.name());
        user.setAuthorities(Role.ROLE_UNVERIFIED_USER.getAuthorities());
        user.setBlobUUID(UUID.randomUUID().toString());
        user.setAvatarUrl(createGithubAvatar(user));
        return userRepository.save(user);
    }

    @Override
    public String createGithubAvatar(User user) {
        String containerName = user.getBlobUUID();
        InputStream avatar = avatarGenerator.generateGithubAvatar();
        return azureStorageService.uploadBlobFromInputStream(containerName, avatar, "avatar.png").toString();
    }

    @Override
    public String encodePassword(String raw) {
        return bCryptPasswordEncoder.encode(raw);
    }

    @Override
    public User changePassword(User user, String password) {
        user.setPassword(this.encodePassword(password));
        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmailAndDeleted(email, false));
    }

    @Override
    public User fromUserDTO(UserDTO userDTO) {
        return boundMapper.map(userDTO);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsernameAndDeleted(username, false));
    }

    @Override
    public void delete(User user) {
        user.setDeleted(true);
        userRepository.save(user);
    }

    /**
     * Return a list of deleted user with a container whose account has been deleted for at least 7 days
     * @param deleteBeforeDate the days since the user has been deleted
     *                         e.g. Assuming current time is Jan 10th,  deleteBeforeDate is 7 days
     *                         a deleted user whose updatedDate is Jan 2nd will be included in the list
     *                         but a deleted user whose updatedDate is Jan 8th won't be included
     * @return
     */
    @Override
    public List<User> findDeletedUserWithContainer(Date deleteBeforeDate) {
        return userRepository.findByDeletedAndUpdatedDateBeforeAndBlobUUIDIsNotNull(true, deleteBeforeDate);
    }

    @Override
    public List<User> saveAll(List<User> users) {
        return userRepository.saveAll(users);
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsernameAndDeleted(username, false);
        if (user == null) {
            logger.error("User not found by username: {}", username);
            throw new UserNotFoundException(username);
        } else {
            return new UserPrincipal(user);
        }
    }

}