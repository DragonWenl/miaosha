package com.lwl.miaosha.redis;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-20
 */
public class GoodsKey extends BasePrefix {

    //考虑页面缓存有效期比较短
    public GoodsKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    //有效期1分钟
    public static GoodsKey getGoodsList = new GoodsKey(60,"gl"); //设置商品key的前缀
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
}
