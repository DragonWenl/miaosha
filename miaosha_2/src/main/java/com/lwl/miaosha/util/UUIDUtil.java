package com.lwl.miaosha.util;

import java.util.UUID;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-18
 */
//用于生成sessionId
public class UUIDUtil {
    //原本就有uuid这个方法，只不过中间有一个“-”，我们把它去掉即可
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public static void main(String[] args) {
        String str = UUID.randomUUID().toString();
        System.out.println(UUID.randomUUID().toString());
    }
}
