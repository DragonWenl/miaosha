package com.lwl.miaosha.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-17
 */
//进行MD5校验
public class Md5Util {
    /*
    输入一个字符串，返回它的MD5码
     */
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1l0o2v2e"; //要加的固定盐

    //第一次进行MD5
    public static String inputPassToFormPass(String inputPassword){
        String str = salt+inputPassword;
        return md5(str);
    }

    //第二次进行MD5,将收到的MD5码进行第二次MD5
    public static String formToDbPass(String inputPassword,String salt){ //这个salt会变成随机
        String str = inputPassword+salt;
        return md5(str);
    }

    public static void main(String[] args) {
        String first = Md5Util.inputPassToFormPass("123456");
        System.out.println(first);
    }
}
