package com.lwl.miaosha.service;

import com.lwl.miaosha.domain.MiaoshaOrder;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.domain.OrderInfo;
import com.lwl.miaosha.redis.MiaoshaKey;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.util.Md5Util;
import com.lwl.miaosha.util.UUIDUtil;
import com.lwl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

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

    //生成一个秒杀path，写入缓存，并且，返回至前台
    public String createMiaoshaPath(MiaoshaUser user, Long goodsId) {
        String str= Md5Util.md5(UUIDUtil.uuid()+"123456");
        //将随机串保存在服务端缓存，并且返回至客户端。
        redisService.set(MiaoshaKey.getMiaoshaPath, user.getId()+"_"+goodsId, str);
        return str;
    }

    //验证path是否正确
    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if (user==null || path==null){
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    //创建验证码
    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        //宽度和高度
        int width = 80;
        int height = 32;
        //创建对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // 设置背景色
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // 开始画
        g.setColor(Color.black); //画笔颜色
        g.drawRect(0, 0, width - 1, height - 1);
        // 生成随机点
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // 生成随机码
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode); //计算验证码的结果
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    //计算结果
    private int calc(String verifyCode) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(verifyCode);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    //生成计算公式的验证码
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    //从Redis中读取验证码，与前端输入的验证码对比校验
    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, Integer.class);

        if(codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        //验证成功后删掉这个验证码
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
        return true;
    }
}
