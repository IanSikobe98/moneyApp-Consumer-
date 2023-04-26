package com.example.moneyapp;


import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

/**
 * @author babaliam
 */
@Configuration
public class RabbitMqConfiguration implements RabbitListenerConfigurer {

    public static final String NOTIFICATION_QUEUE = "VAS.NOTIFICATION_QUEUE";
    public static final String NOTIFICATION_EXCHANGE = "VAS.NOTIFICATION_EXCHANGE";
    public static final String APPROVAL_QUEUE = "VAS.APPROVAL_QUEUE";
    public static final String APPROVAL_EXCHANGE = "VAS.APPROVAL_EXCHANGE";
    public static final String TRANSACTION_QUEUE = "VAS.TRANSACTION_QUEUE";
    public static final String TRANSACTION_EXCHANGE = "VAS.TRANSACTION_EXCHANGE";
    public static final String QUERY_KYC_EXCHANGE = "VAS.QUERY_KYC_EXCHANGE";
    public static final String QUERY_KYC_QUEUE = "VAS.QUERY_KYC_QUEUE";

    private final Environment environment;
//    @Autowired
//    private ConfigCrypto crypto;

    @Autowired
    public RabbitMqConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public ConnectionFactory connectionFactory() throws Exception {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(environment.getRequiredProperty("spring.rabbitmq.host"));
        connectionFactory.setUsername(environment.getRequiredProperty("spring.rabbitmq.username"));
        connectionFactory.setPassword(environment.getRequiredProperty("spring.rabbitmq.password"));
//        connectionFactory.setRequestedHeartBeat(environment
//                .getProperty("datasource.batchapp.rabbit.queueConfigs.heartBeatRate", Integer.class, 60));
        return connectionFactory;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() throws Exception {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());
        return messageHandlerMethodFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() throws Exception {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
/*        factory.setConcurrentConsumers(3);
        factory.setDefaultRequeueRejected(false);
        factory.setMaxConcurrentConsumers(10);*/
        return factory;
    }

    @Bean
    public SimpleMessageListenerContainer getActivityDataListenerContainer() throws Exception {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConcurrentConsumers(10);
        container.setPrefetchCount(10);
        container.setDefaultRequeueRejected(false);
        return container;
    }


}