package com.lwl.miaosha.controller;

import com.lwl.miaosha.domain.MiaoshaOrder;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.domain.OrderInfo;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.result.CodeMsg;
import com.lwl.miaosha.service.GoodsService;
import com.lwl.miaosha.service.MiaoshaService;
import com.lwl.miaosha.service.MiaoshaUserService;
import com.lwl.miaosha.service.OrderService;
import com.lwl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-19
 */
//秒杀功能的实现
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    MiaoshaUserService userService;
    @Autowired
    RedisService redisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;

    @RequestMapping("/do_miaosha")
    public String domiaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) { //可以对传入参数指定参数名
        // 判断用户是否为空，用户若为空，则返回登录页面
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();  //库存
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_FAIL.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrserByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            model.addAttribute("errmsg",CodeMsg.REPEATE_MIAOSHA);//重复秒杀
            return "miaosha_fail";
        }

        //减库存 下订单 写入秒杀订单，需要通过事务实现
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";  //生成订单
    }
}
