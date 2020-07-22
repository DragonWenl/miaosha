package com.lwl.miaosha.dao;

import com.lwl.miaosha.domain.MiaoshaOrder;
import com.lwl.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-19
 */
@Mapper
public interface IOrderDao {

    //输入用户id和商品id，查询用户是否已经秒杀到了这个商品
    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    public MiaoshaOrder getMiaoshaOrserByUserIdGoodsId(@Param("userId")long userId, @Param("goodsId")long goodsId);

    //生成秒杀成功的订单(订单详情)
    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()") //返回值
    public long insert(OrderInfo orderInfo);

    //用户秒杀成功，在miaosha_order中添加成功订单
    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    public int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    public OrderInfo getOrderById(long orderId);
}
