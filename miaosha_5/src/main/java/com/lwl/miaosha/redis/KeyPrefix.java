package com.lwl.miaosha.redis;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-17
 */
public interface KeyPrefix {
    //设置有效期
    public int expireSeconds();

    public  String getPrefix();
}
