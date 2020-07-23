package com.lwl.miaosha.controller;

import com.lwl.miaosha.domain.MiaoshaOrder;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.rabbitmq.MiaoshaMessage;
import com.lwl.miaosha.rabbitmq.RabbitMQSender;
import com.lwl.miaosha.redis.*;
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
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
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
            localOverMap.put(goods.getId(), false);
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

    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
                                   @RequestParam("goodsId") long goodsId,
                                   @PathVariable("path") String path) { //可以对传入参数指定参数名
        // 判断用户是否为空，用户若为空，则返回登录页面
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path路径是否正确
        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEAGAL);
        }
        //判断商品是否卖光了，内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over)
            return Result.error(CodeMsg.MIAOSHA_OVER_ERROR);

        //缓存中商品数量减少1
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) { //商品已经秒杀完了
            localOverMap.put(goodsId, true);
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
    }

    /**
     * 获取秒杀的path,并且验证验证码的值是否正确
     */
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request,Model model, MiaoshaUser user,
                                         @RequestParam("goodsId") Long goodsId, //从前端获取商品id
                                         @RequestParam(value = "verifyCode",defaultValue="0") String verifyCode) { //defaultValue="0"用于测试不需要验证码
        model.addAttribute("user", user);
        //如果用户为空，则返回至登录页面
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //查询该用户访问的次数
        String uri = request.getRequestURI();
        String key = uri + "_" + user.getId();
        Integer num = redisService.get(AccessKey.num, key, Integer.class);
        if (num == null) { //缓存中没有
            redisService.set(AccessKey.num, key, 0);
        } else if (num < 5) { //5秒最多只能访问5次
            redisService.set(AccessKey.num, key, num + 1);
        }else {
            return Result.error(CodeMsg.ACCESS_LIMIT);//访问过于频繁
        }
        //校验验证码
        if (verifyCode.length()==0)
            return Result.error(CodeMsg.VERIFYCODE_EMPTY);
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, Integer.parseInt(verifyCode));
        if (!check) {
            return Result.error(CodeMsg.VERIFYCODE_ERROR);
        }
        //生成一个随机串，作为秒杀的接口地址
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    //这个方法外部一般不要调用，是为了方便自己在测试的时候还原数据库和缓存中的数据用的
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.getGoodsVoList();
        for (GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoshaUser user,
                                              @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
