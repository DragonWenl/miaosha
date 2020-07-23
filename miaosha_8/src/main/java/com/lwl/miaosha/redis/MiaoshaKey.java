package com.lwl.miaosha.redis;

public class MiaoshaKey extends BasePrefix {

    private MiaoshaKey(int seconds, String prefix) {
        super(seconds,prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"go");
    public static KeyPrefix getMiaoshaPath = new MiaoshaKey(10,"mp"); //每过10秒就会失效
    public static KeyPrefix getMiaoshaVerifyCode = new MiaoshaKey(300,"vc"); //验证码5分钟失效
}