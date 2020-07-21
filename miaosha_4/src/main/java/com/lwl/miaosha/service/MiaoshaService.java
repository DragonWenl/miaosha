package com.lwl.miaosha.service;

import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.domain.OrderInfo;
import com.lwl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //秒杀核心操作
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减少库存
        goodsService.reduceStock(goods);
        //写订单
        OrderInfo orderInfo = orderService.createOrder(user, goods);
        return orderInfo;
    }
}
