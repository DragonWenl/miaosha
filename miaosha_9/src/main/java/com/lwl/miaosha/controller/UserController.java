package com.lwl.miaosha.controller;

import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.result.Result;
import com.lwl.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-19
 */
//访问个人用户信息(通过参数),用于压力测试
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    MiaoshaUserService userService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> userInfo(Model model, MiaoshaUser user) {
        return Result.success(user);
    }
}
