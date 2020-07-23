package com.lwl.miaosha.controller;

import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.redis.GoodsKey;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.result.Result;
import com.lwl.miaosha.service.GoodsService;
import com.lwl.miaosha.service.MiaoshaUserService;
import com.lwl.miaosha.vo.GoodsDetailVo;
import com.lwl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/list",produces = {"text/html;charset=UTF-8"})
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
//        return "goods_list";
        //1. 从缓存中取商品信息
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //从数据库中查询商品列表
        List<GoodsVo> goodsList = goodsService.getGoodsVoList();
        model.addAttribute("goodsList", goodsList);

        //如果再缓存中没有取到
        SpringWebContext ctx = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //没有取到缓存，手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)) { //渲染结果保存到缓存，方便下次使用
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    //页面缓存功能展示
    @RequestMapping(value = "/to_detail/{goodsId}",produces = {"text/html;charset=UTF-8"})
    @ResponseBody
    public String toDetail2(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
                           @PathVariable("goodsId") long goodsId) {//id一般用snowflake算法
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        //手动渲染
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
        //return "goods_detail";//返回页面login

        SpringWebContext ctx = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }
        return html;
    }

    //页面静态化功能展示
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
                                        @PathVariable("goodsId") long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startAt) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        return Result.success(vo);
    }

}
