package com.gruapim.collaboration.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia consumida pelo microsserviço: exchange compartilhado, fila própria
 * e uma dead-letter queue para mensagens que falham no processamento.
 */
@Configuration
public class RabbitConfig {

    public static final String EVENTS_EXCHANGE = "gruapim.events";
    public static final String NOTIFICATIONS_QUEUE = "collab.notifications";
    public static final String DLQ = "collab.notifications.dlq";
    public static final String TASK_ASSIGNED_KEY = "task.assigned";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Queue notificationsQueue() {
        return QueueBuilder.durable(NOTIFICATIONS_QUEUE)
                .deadLetterExchange("")            // default exchange
                .deadLetterRoutingKey(DLQ)         // mensagens rejeitadas vão para a DLQ
                .build();
    }

    @Bean
    public Binding taskAssignedBinding() {
        return BindingBuilder.bind(notificationsQueue())
                .to(eventsExchange())
                .with(TASK_ASSIGNED_KEY);
    }

    /**
     * Usa o tipo inferido da assinatura do {@code @RabbitListener} em vez do
     * cabeçalho {@code __TypeId__}, já que o contrato vive em pacotes diferentes
     * no produtor e no consumidor.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
