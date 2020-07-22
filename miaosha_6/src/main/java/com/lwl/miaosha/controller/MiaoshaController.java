package com.lwl.miaosha.controller;

import com.lwl.miaosha.domain.MiaoshaOrder;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.domain.OrderInfo;
import com.lwl.miaosha.rabbitmq.MiaoshaMessage;
import com.lwl.miaosha.rabbitmq.RabbitMQSender;
import com.lwl.miaosha.redis.GoodsKey;
import com.lwl.miaosha.redis.MiaoshaKey;
import com.lwl.miaosha.redis.OrderKey;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.result.CodeMsg;
import com.lwl.miaosha.result.Result;
import com.lwl.miaosha.service.GoodsService;
import com.lwl.miaosha.service.MiaoshaService;
import com.lwl.miaosha.service.MiaoshaUserService;
import com.lwl.miaosha.service.OrderService;
import com.lwl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-19
 */
//秒杀功能的实现
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
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
    @Autowired
    RabbitMQSender sender;

    private HashMap<Long, Boolean> localOverMap = new HashMap<>(); //记录

    //系统初始化，将数据库中商品的数量进行预减少后加入到缓存,在容器启动的时候，检测到了实现了接口InitializingBean之后，就回去回调afterPropertiesSet方法
    @Override
    public void afterPropertiesSet() throws Exception {
        //获取秒杀商品列表
        List<GoodsVo> goodsList = goodsService.getGoodsVoList();
        if (goodsList == null) {
            return;
        }
        for (GoodsVo goods : goodsList) {
            //GoodsKey.getMiaoshaGoodsStock秒杀的商品数量
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            //设置商品为有库存
            localOverMap.put(goods.getId(),false);
        }
    }

    /**
     * 轮询秒杀结果：
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //从缓存中查询状态
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) { //可以对传入参数指定参数名
        // 判断用户是否为空，用户若为空，则返回登录页面
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //判断商品是否卖光了
        boolean over = localOverMap.get(goodsId);
        if(over)
            return Result.error(CodeMsg.MIAOSHA_OVER_ERROR);

        //缓存中商品数量减少1
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) { //商品已经秒杀完了
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_OVER_ERROR);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrserByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);//重复秒杀
        }
        //请求入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setMiaoshaUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(0);//排队中

        //使用消息队列之前的代码：
        /*判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();  //库存
        if (stock <= 0) {
            return Result.error(CodeMsg.MIAOSHA_OVER_ERROR);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrserByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);//重复秒杀
        }

        //减库存 下订单 写入秒杀订单，需要通过事务实现
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);  //生成订单
         */
    }

    //这个方法外部一般不要调用，是为了方便自己在测试的时候还原数据库和缓存中的数据用的
    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.getGoodsVoList();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }
}
