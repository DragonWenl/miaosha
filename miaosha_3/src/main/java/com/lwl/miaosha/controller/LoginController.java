package com.lwl.miaosha.controller;

import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.result.CodeMsg;
import com.lwl.miaosha.result.Result;
import com.lwl.miaosha.service.MiaoshaUserService;
import com.lwl.miaosha.util.ValidatorUtil;
import com.lwl.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-17
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody  //将方法的返回值,以特定的格式写入到response的body区域
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){ //需要校验的参数前面加@Valid
        log.info(loginVo.toString());
        //登录
        userService.login(response,loginVo);
        return Result.success(true);
    }
}
