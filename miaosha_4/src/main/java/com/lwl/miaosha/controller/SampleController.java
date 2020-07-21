package com.lwl.miaosha.controller;

import com.lwl.miaosha.domain.User;
import com.lwl.miaosha.redis.KeyPrefix;
import com.lwl.miaosha.redis.RedisConfig;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.redis.UserKey;
import com.lwl.miaosha.result.Result;
import com.lwl.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-16
 */
@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/db/get")
    @ResponseBody  //用于控制层，将方法的返回值,以特定的格式写入到response的body区域
    public Result<User> dbGet(){
        User user = userService.getUserById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById,"2", User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User(2, "zhaosi");
        boolean res = redisService.set(UserKey.getById,"2", user); //如果只有两个参数，另一个线程修改了key2的类型怎么办，增加前缀
        return Result.success(res);
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","liwenlong");
        return "hello";
    }
}
