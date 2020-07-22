package com.lwl.miaosha.service;

import com.lwl.miaosha.dao.IOrderDao;
import com.lwl.miaosha.domain.MiaoshaOrder;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.domain.OrderInfo;
import com.lwl.miaosha.redis.OrderKey;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-19
 */
@Service
public class OrderService {
    @Autowired(required = false)
    IOrderDao orderDao;

    @Autowired
    RedisService redisService;

    //判断用户是否已经秒杀了商品，防止重复秒杀
    public MiaoshaOrder getMiaoshaOrserByUserIdGoodsId(Long userId, long goodsId) {
        //从数据库中查询
        //MiaoshaOrder order = orderDao.getMiaoshaOrserByUserIdGoodsId(userId, goodsId);
        //从Redis中查询订单
        MiaoshaOrder order = redisService.get(OrderKey.getMiaoshaOrderByUidGid, userId+ "_"+goodsId, MiaoshaOrder.class);
        return order;
    }

    //根据订单id获取订单详情
    public OrderInfo getOrderById(long orderId) {
        OrderInfo orderById = orderDao.getOrderById(orderId);
        return orderById;
    }

    //生成订单,生成的订单要写入缓存
    @Transactional  //事务
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(115156L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        long orderId = orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder); //订单放入数据库
        //订单放入缓存
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, user.getId()+ "_"+goods.getId(), miaoshaOrder);
        return orderInfo;
    }
}
