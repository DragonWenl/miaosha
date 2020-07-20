package com.lwl.miaosha.controller;

import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.service.GoodsService;
import com.lwl.miaosha.service.MiaoshaUserService;
import com.lwl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-18
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService userService;
    @Autowired
    RedisService redisService;
    @Autowired
    GoodsService goodsService;

    @RequestMapping("/list")
    public String toList(Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
        //从数据库中查询商品列表
        List<GoodsVo> goodsList = goodsService.getGoodsVoList();
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    @RequestMapping("/detail/{goodsId}")
    public String toDetail(Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId) {//id一般用snowflake算法
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);
        //既然是秒杀，还要传入秒杀开始时间，结束时间等信息
        long start = goods.getStartDate().getTime();
        long end = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        //秒杀状态量
        int status = 0;
        //开始时间倒计时
        int remainSeconds = 0;
        //查看当前秒杀状态
        if (now < start) {//秒杀还未开始，--->倒计时
            status = 0;  //表示未开始
            remainSeconds = (int) ((start - now) / 1000);  //毫秒转为秒
        } else if (now > end) { //秒杀已经结束
            status = 2; //表示结束
            remainSeconds = -1;  //毫秒转为秒
        } else {
            status = 1;  //秒杀正在进行
            remainSeconds = 0;  //毫秒转为秒
        }
        model.addAttribute("status", status);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";//返回页面login
    }
}
