package com.lwl.miaosha.controller;

import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.redis.MiaoshaKey;
import com.lwl.miaosha.result.CodeMsg;
import com.lwl.miaosha.result.Result;
import com.lwl.miaosha.service.MiaoshaService;
import com.lwl.miaosha.service.MiaoshaUserService;
import com.lwl.miaosha.vo.LoginVo;
import com.lwl.miaosha.vo.RegisterVo;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Random;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-23
 */
@Controller
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    MiaoshaUserService miaoshaUserService;

    private static Logger log = LoggerFactory.getLogger(RegisterController.class);

    @RequestMapping("/to_register")
    public String toRegister(){
        return "register";
    }

    @RequestMapping("/do_register")  //可以在这里输入验证码
    @ResponseBody  //将方法的返回值,以特定的格式写入到response的body区域
    public Result<String> doRegiste(@Valid RegisterVo registerVo){ //需要校验的参数前面加@Valid
        log.info(registerVo.toString());
        //注册
        boolean success = userService.register(registerVo);
        if(success){
            return Result.success("");
        }else return Result.error(CodeMsg.REGISTER_ERROR);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getRegistVerifyCode(HttpServletResponse response, RegisterVo user) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image = miaoshaUserService.createVerifyCode(user);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}
