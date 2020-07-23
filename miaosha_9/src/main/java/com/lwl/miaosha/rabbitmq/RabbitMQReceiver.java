package com.lwl.miaosha.rabbitmq;

import com.lwl.miaosha.domain.MiaoshaOrder;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.domain.OrderInfo;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.result.CodeMsg;
import com.lwl.miaosha.result.Result;
import com.lwl.miaosha.service.GoodsService;
import com.lwl.miaosha.service.MiaoshaService;
import com.lwl.miaosha.service.OrderService;
import com.lwl.miaosha.vo.GoodsVo;
import org.apache.coyote.OutputBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-21
 */
//消息的接收方
@Service
public class RabbitMQReceiver {
    @Autowired
    RedisService redisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;

    private static Logger log = LoggerFactory.getLogger(RabbitMQReceiver.class);

    //从队列中取出订单
    @RabbitListener(queues = "miaosha.queue")
    public void receive(String message) {
        log.info("Receive Messages:" + message);
        MiaoshaMessage mm = redisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getMiaoshaUser();
        long goodsId = mm.getGoodsId();
        //判断库存,从数据库中查询
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();  //库存
        if (stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了(从缓存中查)
        MiaoshaOrder order = orderService.getMiaoshaOrserByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            return;//重复秒杀
        }
        //减库存 下订单 写入秒杀订单，需要通过事务实现
        miaoshaService.miaosha(user, goods);
    }

//    @RabbitListener(queues = "topic.queue1")
//    public void receiveTopic1(String message) {
//        log.info(" topic queue1 message:" + message);
//    }
//
//    @RabbitListener(queues = "topic.queue2")
//    public void receiveTopic2(String message) {
//        log.info(" topic queue2 message:" + message);
//    }

//    @RabbitListener(queues = "header.queue")
//    public void receiveHeaderQueue(byte[] message) {
//        log.info("header queue message:" + new String(message));
//    }
}
