package tech.eportfolio.server.service.impl;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.eportfolio.server.constant.Role;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.EmailExistException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.UsernameExistException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Random random = new Random(System.currentTimeMillis());

    @Autowired
    BoundMapperFacade<UserDTO, User> boundMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public BoundMapperFacade<UserDTO, User> boundMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        return mapperFactory.getMapperFacade(UserDTO.class, User.class);
    }


    @Override
    public User register(User user) {
        Optional<User> emailResult = findUserByEmail(user.getEmail());
        if (emailResult.isPresent()) {
            throw new EmailExistException(user.getEmail());
        }

        if (StringUtils.isEmpty(user.getUsername())) {
            user.setUsername(user.getFirstName() + user.getLastName() + random.nextInt());
        }

        Optional<User> userNameResult = findUserByUsername(user.getUsername());
        if (userNameResult.isPresent()) {
            throw new UsernameExistException(user.getUsername());
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(userRepository.findById(id));
    }

    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    @Override
    public User fromUserDTO(UserDTO userDTO) {
        return boundMapper.map(userDTO);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            LOGGER.error("User not found by username: {}", username);
            throw new UserNotFoundException(username);
        } else {
            LOGGER.info("Found user by username: {}", username);
            return new UserPrincipal(user);
        }
    }
}