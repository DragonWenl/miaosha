package com.lwl.miaosha.service;

import com.lwl.miaosha.dao.IOrderDao;
import com.lwl.miaosha.domain.MiaoshaOrder;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.domain.OrderInfo;
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

    //判断用户是否已经秒杀了商品，防止重复秒杀
    public MiaoshaOrder getMiaoshaOrserByUserIdGoodsId(Long userId, long goodsId) {
        MiaoshaOrder order = orderDao.getMiaoshaOrserByUserIdGoodsId(userId, goodsId);
        return order;
    }

    //生成订单
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
        return orderInfo;
    }
}
