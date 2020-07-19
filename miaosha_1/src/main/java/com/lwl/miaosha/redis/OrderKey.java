package com.lwl.miaosha.redis;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-17
 */
public class OrderKey extends BasePrefix{
    public OrderKey(int expireSeconds,String prefix){
        super(expireSeconds,prefix);
    }
}
