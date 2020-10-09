package tech.eportfolio.server.service.impl;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.exception.UserFollowExistException;
import tech.eportfolio.server.common.exception.UserFollowNotExistException;
import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserFollow;
import tech.eportfolio.server.repository.UserFollowRepository;
import tech.eportfolio.server.service.UserFollowService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserFollowServiceImpl implements UserFollowService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AmqpAdmin admin;

    private final RabbitTemplate rabbitTemplate;

    private final UserFollowRepository userFollowerRepository;

    @Autowired
    public UserFollowServiceImpl(AmqpAdmin admin, RabbitTemplate rabbitTemplate, UserFollowRepository userFollowerRepository) {
        this.admin = admin;
        this.rabbitTemplate = rabbitTemplate;
        this.userFollowerRepository = userFollowerRepository;
    }

    @Override
    public List<UserFollow> findBySourceUser(User sourceUser) {
        return userFollowerRepository.findBySourceUsernameAndDeleted(sourceUser.getUsername(), false);
    }

    /**
     * Notify one's followers about a activity
     *
     * @param activity the activity
     * @param username the username of the related user who created this activity
     */
    @Override
    public void notifyFollower(Activity activity, String username) {
        byte[] payload = SerializationUtils.serialize(activity);
        Message message = MessageBuilder.withBody(payload).build();
        logger.info("Message send to exchange: {} body: {}", username, activity);
        // Send a message to the user's exchange, which will send one message to each follower's queue
        rabbitTemplate.send(username, message);
    }

    /**
     * Create a message queue and a fanout exchange for the given user.
     *
     * @param username username
     */
    @Override
    public void createQueueAndExchange(String username) {
        // if user doesn't have a message queue yet
        if (admin.getQueueProperties(username) == null) {
            Queue queue = new Queue(username, true);
            FanoutExchange exchange = new FanoutExchange(username, true, false);
            admin.declareQueue(queue);
            logger.info("Message queue created: {} {}", queue.getName(), queue.getArguments());
            // declare a fanout exchange using username. Messages sent to this fanout exchange will be routed to
            // bound queues (i.e. followers' queues)
            admin.declareExchange(exchange);
            logger.info("{} exchange created: {}", exchange.getType(), exchange.getName());
        }
    }

    @Override
    public List<UserFollow> findByDestinationUser(User destinationUser) {
        return userFollowerRepository.findByDestinationUsernameAndDeleted(destinationUser.getUsername(), false);
    }

    private Binding createBinding(String sourceUsername, String destinationUsername) {
        return new Binding(sourceUsername, Binding.DestinationType.QUEUE, destinationUsername, "", null);
    }

    private void bind(String sourceUsername, String destinationUsername) {
        Binding binding = createBinding(sourceUsername, destinationUsername);
        admin.declareBinding(binding);
        logger.info("Bound queue '{}' to exchange '{}'", sourceUsername, destinationUsername);
    }

    private void unbind(String sourceUsername, String destinationUsername) {
        Binding binding = createBinding(sourceUsername, destinationUsername);
        admin.removeBinding(binding);
        logger.info("Unbound queue '{}' from exchange '{}'", sourceUsername, destinationUsername);
    }


    @Override
    public UserFollow follow(User sourceUser, String destinationUsername) {
        Optional<UserFollow> follower = this.findBySourceUsernameAndDestinationNameAndDeleted(sourceUser.getUsername(), destinationUsername, false);
        if (follower.isPresent()) {
            UserFollow userFollower = follower.get();
            if (userFollower.isDeleted()) {
                userFollower.setDeleted(false);
                userFollower.setCreatedDate(new Date());
                return userFollowerRepository.save(userFollower);
            } else {
                throw new UserFollowExistException(sourceUser.getUsername(), destinationUsername);
            }
        }
        UserFollow newUserFollower = new UserFollow();
        newUserFollower.setSourceUsername(sourceUser.getUsername());
        newUserFollower.setDestinationUsername(destinationUsername);
        newUserFollower.setSourceUserId(sourceUser.getId());
        bind(sourceUser.getUsername(), destinationUsername);
        return userFollowerRepository.save(newUserFollower);
    }

    @Override
    public UserFollow unfollow(User sourceUser, String destinationUsername) {
        String username = sourceUser.getUsername();
        UserFollow userFollow = this.findBySourceUsernameAndDestinationNameAndDeleted(sourceUser.getUsername(), destinationUsername, false).orElseThrow(
                () -> new UserFollowNotExistException(username, destinationUsername));
        unbind(sourceUser.getUsername(), destinationUsername);
        return this.delete(userFollow);
    }


    @Override
    public Optional<UserFollow> findBySourceUsernameAndDestinationNameAndDeleted(String sourceUser, String destinationUsername, boolean deleted) {
        return Optional.ofNullable(userFollowerRepository.findBySourceUsernameAndDestinationUsernameAndDeleted(sourceUser, destinationUsername, deleted));
    }

    @Override
    public UserFollow delete(UserFollow userFollow) {
        userFollow.setDeleted(true);
        userFollowerRepository.save(userFollow);
        return userFollow;
    }
}
