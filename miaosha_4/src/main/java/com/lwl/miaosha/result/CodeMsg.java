package com.lwl.miaosha.result;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-16
 */
public class CodeMsg {
    private int code;
    private String msg;

    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_ERROR=new CodeMsg(500101,"参数校验异常:%s");
    public static CodeMsg REQUEST_ILLEAGAL=new CodeMsg(500102,"非法请求!");
    public static CodeMsg MIAOSHA_FAIL=new CodeMsg(500103,"秒杀失败!");
    public static CodeMsg ACCESS_LIMIT=new CodeMsg(500104,"达到访问限制次数，访问太频繁!");

    //登录模块  5002XX
    public static CodeMsg SESSION_ERROR=new CodeMsg(500210,"session失效!");
    public static CodeMsg PASSWORD_EMPTY=new CodeMsg(500211,"密码不能为空!");
    public static CodeMsg MOBILE_EMPTY=new CodeMsg(500212,"手机号不能为空!");
    public static CodeMsg MOBILE_ERROR=new CodeMsg(500213,"手机号格式错误!");
    public static CodeMsg MOBILE_NOTEXIST=new CodeMsg(500214,"该手机号未注册!");
    public static CodeMsg PASSWORD_ERROR=new CodeMsg(500215,"密码错误!");

    //商品模块  5003XX


    //订单模块  5004XX
    public static CodeMsg ORDER_NOT_EXIST=new CodeMsg(500410,"订单不存在!");

    //秒杀模块  5005XX
    public static CodeMsg MIAOSHA_OVER_ERROR=new CodeMsg(500500,"商品秒杀完毕，库存不足!");
    public static CodeMsg REPEATE_MIAOSHA=new CodeMsg(500500,"不能重复秒杀!");


    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args) { //对Msg加一个参数
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
