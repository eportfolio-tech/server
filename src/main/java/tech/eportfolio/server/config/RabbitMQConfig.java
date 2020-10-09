package tech.eportfolio.server.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String topicExchangeName = "spring-boot-exchange";

    public static final String queueName = "spring-boot";

//    @Bean
//    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        RetryTemplate retryTemplate = new RetryTemplate();
//        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
//        backOffPolicy.setInitialInterval(500);
//        backOffPolicy.setMultiplier(10.0);
//        backOffPolicy.setMaxInterval(10000);
//        retryTemplate.setBackOffPolicy(backOffPolicy);
//        template.setRetryTemplate(retryTemplate);
//        return template;
//    }

//    @Bean
//    Queue queue() {
//        return new Queue(queueName, false);
//    }
//
//    @Bean
//    TopicExchange exchange() {
//        return new TopicExchange(topicExchangeName);
//    }
//
//    @Bean
//    Binding binding(Queue queue, TopicExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
//    }
//
//    @Bean
//    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
//                                             MessageListenerAdapter listenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(queueName);
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }
//
//    @Bean
//    MessageListenerAdapter listenerAdapter(Receiver receiver) {
//        return new MessageListenerAdapter(receiver, "receiveMessage");
//    }

    /**
     * Required for executing adminstration functions against an AMQP Broker
     */
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
