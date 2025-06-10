package com.earthlog.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration for EarthLog
 * 
 * Exchange: earthlog.events (Topic Exchange)
 * Queues:
 *   - activity.created    : Handles new activity events
 *   - goal.progress       : Handles goal progress monitoring
 *   - badge.evaluation    : Handles badge/achievement evaluation
 *   - notification.email  : Handles email notification queue
 */
@Configuration
public class RabbitMQConfig {

    // Exchange name
    public static final String EXCHANGE_NAME = "earthlog.events";

    // Queue names
    public static final String ACTIVITY_QUEUE = "activity.created";
    public static final String GOAL_PROGRESS_QUEUE = "goal.progress";
    public static final String BADGE_EVALUATION_QUEUE = "badge.evaluation";
    public static final String EMAIL_NOTIFICATION_QUEUE = "notification.email";

    // Routing keys
    public static final String ACTIVITY_ROUTING_KEY = "activity.created";
    public static final String GOAL_ROUTING_KEY = "goal.#";
    public static final String BADGE_ROUTING_KEY = "badge.#";
    public static final String EMAIL_ROUTING_KEY = "notification.email";

    // Dead Letter Exchange for failed messages
    public static final String DLX_EXCHANGE = "earthlog.dlx";
    public static final String DLQ_QUEUE = "earthlog.dlq";

    // ==================== Exchange Declarations ====================

    @Bean
    public TopicExchange earthlogExchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(DLX_EXCHANGE)
                .durable(true)
                .build();
    }

    // ==================== Queue Declarations ====================

    @Bean
    public Queue activityQueue() {
        return QueueBuilder
                .durable(ACTIVITY_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(DLQ_QUEUE)
                .build();
    }

    @Bean
    public Queue goalProgressQueue() {
        return QueueBuilder
                .durable(GOAL_PROGRESS_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(DLQ_QUEUE)
                .build();
    }

    @Bean
    public Queue badgeEvaluationQueue() {
        return QueueBuilder
                .durable(BADGE_EVALUATION_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(DLQ_QUEUE)
                .build();
    }

    @Bean
    public Queue emailNotificationQueue() {
        return QueueBuilder
                .durable(EMAIL_NOTIFICATION_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(DLQ_QUEUE)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(DLQ_QUEUE)
                .build();
    }

    // ==================== Binding Declarations ====================

    @Bean
    public Binding activityBinding(Queue activityQueue, TopicExchange earthlogExchange) {
        return BindingBuilder
                .bind(activityQueue)
                .to(earthlogExchange)
                .with(ACTIVITY_ROUTING_KEY);
    }

    @Bean
    public Binding goalProgressBinding(Queue goalProgressQueue, TopicExchange earthlogExchange) {
        return BindingBuilder
                .bind(goalProgressQueue)
                .to(earthlogExchange)
                .with(GOAL_ROUTING_KEY);
    }

    @Bean
    public Binding badgeEvaluationBinding(Queue badgeEvaluationQueue, TopicExchange earthlogExchange) {
        return BindingBuilder
                .bind(badgeEvaluationQueue)
                .to(earthlogExchange)
                .with(BADGE_ROUTING_KEY);
    }

    @Bean
    public Binding emailNotificationBinding(Queue emailNotificationQueue, TopicExchange earthlogExchange) {
        return BindingBuilder
                .bind(emailNotificationQueue)
                .to(earthlogExchange)
                .with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLQ_QUEUE);
    }

    // ==================== Message Converter ====================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setExchange(EXCHANGE_NAME);
        return rabbitTemplate;
    }
}
