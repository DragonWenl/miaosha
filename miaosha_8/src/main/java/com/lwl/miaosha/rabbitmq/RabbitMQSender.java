package com.lwl.miaosha.rabbitmq;

import com.lwl.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-21
 */
//消息的发送方
@Service
public class RabbitMQSender {
    @Autowired
    RedisService redisService;

    @Autowired
    AmqpTemplate amqpTemplate;

    private static Logger log = LoggerFactory.getLogger(RabbitMQSender.class);

    //发送秒杀信息，使用Derict模式的交换机。（包含秒杀用户信息，秒杀商品id）
    public void send(Object message){
        String msg = redisService.beanToString(message);
        log.info("Send Messages:"+message);
        // 第一个参数队列的名字，第二个参数发出的信息
        amqpTemplate.convertAndSend("queue",message);
    }


    public void sendTopic(Object message) {
		String msg = redisService.beanToString(message);
		log.info("send topic message:"+ msg);
		amqpTemplate.convertAndSend("topicExchage", "topic.key1", msg+"1");
		amqpTemplate.convertAndSend("topicExchage", "topic.key2", msg+"2");
	}

    	public void sendFanout(Object message) {
		String msg = redisService.beanToString(message);
		log.info("send fanout message:"+msg);
		amqpTemplate.convertAndSend("fanoutxchage", "", msg);
	}

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = redisService.beanToString(mm);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend("miaosha.queue", msg);
    }

//    public void sendHeader(Object message) {
//        String msg = redisService.beanToString(message);
//        log.info("send header message:" + msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("header1", "value1");
//        properties.setHeader("header2", "value2");
//        Message obj = new Message(msg.getBytes(), properties);
//        amqpTemplate.convertAndSend("headersExchage", "", obj);
//    }
}
