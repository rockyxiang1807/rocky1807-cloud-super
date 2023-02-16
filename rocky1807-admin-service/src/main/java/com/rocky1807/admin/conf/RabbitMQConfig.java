package com.rocky1807.admin.conf;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {

    //业务交换机
    @Value("${my.exchange.name}")
    public static String EXCHANGE_THIRDPARTY;
    //业务队列1
    @Value("${my.queue.name}")
    public static String QUEUE_SAVE;
    //业务队列1的key
    @Value("${my.key.name}")
    public static String ROUTINGKEY_SAVE;

    //死信交换机
    @Value("${my.exchange.deadname}")
    public static String EXCHANGE_THIRDPARTY_DEAD;
    //死信队列1
    @Value("${my.queue.deadname}")
    public static String QUEUE_SAVE_DEAD;
    //死信队列1的key
    @Value("${my.key.deadname}")
    public static String ROUTINGKEY_SAVE_DEAD;


    /**
     * 声明死信交换机
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean("exchangeEeDead")
    public DirectExchange exchangePhcpDead() {
        return new DirectExchange(EXCHANGE_THIRDPARTY_DEAD);
    }

    /**
     * 声明死信队列1
     *
     * @return
     */
    @Bean("queueSsDead")
    public Queue queueCompanyDead() {
        return new Queue(QUEUE_SAVE_DEAD);
    }

    /**
     * 声明业务交换机
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean("exchangeEeParty")
    public DirectExchange exchangePhcp() {
        return new DirectExchange(EXCHANGE_THIRDPARTY);
    }


    /**
     * 声明业务队列1
     *
     * @return
     */
    @Bean("queueSs")
    public Queue queueCompany() {
        Map<String,Object> arguments = new HashMap<>(2);
        arguments.put("x-dead-letter-exchange",EXCHANGE_THIRDPARTY_DEAD);
        //绑定该队列到死信交换机的队列1
        arguments.put("x-dead-letter-routing-key",ROUTINGKEY_SAVE_DEAD);
        return QueueBuilder.durable(QUEUE_SAVE).withArguments(arguments).build();
    }

    /**
     * 绑定死信队列1和死信交换机
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding bindingQueueProjectDead(@Qualifier("queueSsDead") Queue queue, @Qualifier("exchangeEeDead") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(RabbitMQConfig.ROUTINGKEY_SAVE_DEAD);
    }


    /**
     * 绑定业务队列1和业务交换机
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding bindingQueueProject(@Qualifier("queueSs") Queue queue, @Qualifier("exchangeEeParty") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(RabbitMQConfig.ROUTINGKEY_SAVE);
    }

}
