package com.lwl.miaosha.redis;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-17
 */
public abstract class BasePrefix implements KeyPrefix {
    private int expireSeconds; //过期时间
    private String prefix;

    public BasePrefix(int expireSeconds,String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix){  //0代表永不过期
        this.expireSeconds = 0;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        //得到类名
        String simpleName = getClass().getSimpleName();
        return simpleName+":"+ prefix;
    }
}
