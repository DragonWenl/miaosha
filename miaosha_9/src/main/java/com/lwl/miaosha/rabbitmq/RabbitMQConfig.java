package com.lwl.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-21
 */
//@Configuration用于定义配置类，可替换xml配置文件，被注解的类内部包含有一个或多个被@Bean注解的方法
@Configuration
public class RabbitMQConfig {
    /**
     * Direct模式 交换机Exchange
     * */
    @Bean
    public Queue queue(){
        //名称，是否持久化
        return new Queue("miaosha.queue",true);
    }

//    /**
//     * Topic模式 交换机Exchange,可以使用通配符匹配
//     * */
//    //新定义一个队列
//    @Bean
//    public Queue topicQueue1(){
//        //名称，是否持久化
//        return new Queue("topic.queue1",true);
//    }
//    //新定义一个队列
//    @Bean
//    public Queue topicQueue2(){
//        //名称，是否持久化
//        return new Queue("topic.queue2",true);
//    }
//    //新定义一个交换机
//    @Bean
//    public TopicExchange topicExchage(){
//        return new TopicExchange("topicExchage");
//    }
//    //队列1与交换机绑定
//    @Bean
//    public Binding topicBinding1() {
//        return BindingBuilder.bind(topicQueue1()).to(topicExchage()).with("topic.key1");
//    }
//    //队列2与交换机绑定
//    @Bean
//    public Binding topicBinding2() {
//        return BindingBuilder.bind(topicQueue2()).to(topicExchage()).with("topic.#");
//    }
//
//    /**
//     * Fanout模式 交换机Exchange
//     * */
//    //新建一个广播类型的交换机
//    @Bean
//    public FanoutExchange fanoutExchage(){
//        return new FanoutExchange("fanoutxchage");
//    }
//    @Bean
//    public Binding FanoutBinding1() {
//        return BindingBuilder.bind(topicQueue1()).to(fanoutExchage());
//    }
//    @Bean
//    public Binding FanoutBinding2() {
//        return BindingBuilder.bind(topicQueue2()).to(fanoutExchage());
//    }

    /**
     * Header模式 交换机Exchange ,使用header中的 key/value（键值对）匹配队列。
     * */
//    @Bean
//    public HeadersExchange headersExchage(){
//        return new HeadersExchange("headersExchage");
//    }
//    @Bean
//    public Queue headerQueue1() {
//        return new Queue("headers.queue", true);
//    }
//
//    @Bean
//    public Binding headerBinding() {
//        Map<String, Object> map = new HashMap();
//        map.put("header1", "value1");
//        map.put("header2", "value2");
//        return BindingBuilder.bind(headerQueue1()).to(headersExchage()).whereAll(map).match();
//    }

}
