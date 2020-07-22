package com.lwl.miaosha.service;

import com.lwl.miaosha.domain.MiaoshaOrder;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.domain.OrderInfo;
import com.lwl.miaosha.redis.MiaoshaKey;
import com.lwl.miaosha.redis.MiaoshaUserKey;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-19
 */
@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;

    private void setGoodsOver(Long goodsId) {
        //设置商品卖光了
        redisService.set(MiaoshaKey.isGoodsOver, goodsId + "", true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exitsKey(MiaoshaKey.isGoodsOver, goodsId + "");
    }

    //秒杀核心操作
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减少库存
        boolean success = goodsService.reduceStock(goods);
        if (!success) {
            setGoodsOver(goods.getId());//标记商品已经被秒杀完
            return null;
        }
        //写订单
        OrderInfo orderInfo = orderService.createOrder(user, goods);
        return orderInfo;
    }

    public long getMiaoshaResult(Long id, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrserByUserIdGoodsId(id, goodsId);
        if (order != null) { //秒杀成功
            return order.getOrderId();
        } else if (getGoodsOver(goodsId)) {//商品卖没了
            return -1;
        } else {  //还在排队
            return 0;
        }
    }

    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }
}
