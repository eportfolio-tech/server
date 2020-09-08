package tech.eportfolio.server.service.impl;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
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
import tech.eportfolio.server.constant.Authority;
import tech.eportfolio.server.constant.Role;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.EmailExistException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.UsernameExistException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.utility.JWTTokenProvider;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Random random = new Random(System.currentTimeMillis());

    private BoundMapperFacade<UserDTO, User> boundMapper;

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private JWTTokenProvider verificationTokenProvider;

    @Autowired
    public void setBoundMapper(BoundMapperFacade<UserDTO, User> boundMapper) {
        this.boundMapper = boundMapper;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setbCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    public void setVerificationTokenProvider(JWTTokenProvider verificationTokenProvider) {
        this.verificationTokenProvider = verificationTokenProvider;
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
        return userRepository.save(user);
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
    public User verify(@NotNull User user, @NotEmpty String token) {
        if (StringUtils.equals(user.getRoles(), Role.ROLE_VERIFIED_USER.name())) {
            // TODO: Create an UserVerificationException
            throw new RuntimeException("User already verified");
        }
        String secret = getVerificationSecret(user);
        JWTVerifier jwtVerifier = verificationTokenProvider.getJWTVerifier(secret);
        if (verificationTokenProvider.isTokenValid(user.getUsername(), token, secret)
                && StringUtils.equals(jwtVerifier.verify(token).getSubject(), user.getUsername())) {
            user.setRoles(Role.ROLE_VERIFIED_USER.name());
            user.setAuthorities(Authority.VERIFIED_USER_AUTHORITIES);
            return userRepository.save(user);
        } else {
            throw new JWTVerificationException("JWT is invalid");
        }
    }

    @Override
    public User passwordRecovery(@NotNull User user, @NotEmpty String token, String newPassword) {
        String secret = getPasswordRecoverySecret(user);
        JWTVerifier jwtVerifier = verificationTokenProvider.getJWTVerifier(secret);
        if (verificationTokenProvider.isTokenValid(user.getUsername(), token, secret)
                && StringUtils.equals(jwtVerifier.verify(token).getSubject(), user.getUsername())) {
            return changePassword(user, newPassword);
        } else {
            throw new JWTVerificationException("JWT is invalid");
        }
    }

    @Override
    public String generateVerificationToken(User user) {
        return verificationTokenProvider.generateJWTToken(new UserPrincipal(user), getVerificationSecret(user));
    }

    @Override
    public String generatePasswordRecoveryToken(User user) {
        return verificationTokenProvider.generateJWTToken(new UserPrincipal(user), getPasswordRecoverySecret(user));
    }

    @Override
    public String getVerificationSecret(@NotNull User user) {
        return user.getUsername() + user.getCreatedAt();
    }

    @Override
    public String getPasswordRecoverySecret(User user) {
        return user.getPassword();
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