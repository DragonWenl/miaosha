package com.lwl.miaosha.redis;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-18
 */
//用来生成用户的id，也就是sessionid
public class MiaoshaUserKey extends BasePrefix{
    public static final int TOKEN_EXPIRE = 3600 * 24 ;       // token 过期时间, 1天

    /*
    expireSecond  过期时间
    prefix
     */
    private MiaoshaUserKey(int expireSecond,String prefix){
        super(expireSecond,prefix);  //只传一个参数，没有过期时间
    }
    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"tx");
    //用户信息可以永久有效
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0,"id");//永久有效
}
