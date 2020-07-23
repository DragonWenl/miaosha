package com.lwl.miaosha.service;

import com.lwl.miaosha.dao.IMiaoshaUserDao;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.exception.GlobalException;
import com.lwl.miaosha.redis.MiaoshaKey;
import com.lwl.miaosha.redis.MiaoshaUserKey;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.result.CodeMsg;
import com.lwl.miaosha.util.Md5Util;
import com.lwl.miaosha.util.UUIDUtil;
import com.lwl.miaosha.vo.LoginVo;
import com.lwl.miaosha.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-17
 */
//登录校验功能
@Service
public class MiaoshaUserService {
    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired(required = false)
    IMiaoshaUserDao miaoshaUserDao;

    //把用户信息放入到缓存中,先取缓存，缓存没有取数据库
    public MiaoshaUser getById(long id) {
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, id + "", MiaoshaUser.class);
        if (user != null) { //缓存中有数据
            return user;
        }
        //缓存中没有的话，从数据库中查找，保存到缓存中
        user = miaoshaUserDao.getById(id);
        if (user != null) {//写入缓存
            redisService.set(MiaoshaUserKey.getById, id + "", user);
        }
        return user;
    }

    //修改密码
    public boolean updatePassword(String token, long id, String passwordNew) {
        //取出user
        MiaoshaUser user = getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.BIND_ERROR);
        }
        //更新数据库
        MiaoshaUser newUser = new MiaoshaUser();
        newUser.setId(id);
        newUser.setPassword(Md5Util.formToDbPass(passwordNew, user.getSalt()));
        miaoshaUserDao.update(newUser);
        //缓存中用户信息的修改
        redisService.delete(MiaoshaUserKey.getById, id + "");
        user.setPassword(newUser.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String inputPassword = loginVo.getPassword();
        //判断手机号在数据库中是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) { //用户不存在
            throw new GlobalException(CodeMsg.MOBILE_NOTEXIST);
        }
        //验证登录密码
        String dbPassword = user.getPassword();
        String dbSalt = user.getSalt();
        //对输入密码的MD5码进行第二次MD5加密
        String doubleMD5Pass = Md5Util.formToDbPass(inputPassword, dbSalt);
        if (!dbPassword.equals(doubleMD5Pass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR); //密码输入错误
        }
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    //生成cookie
    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        //生成cookie,存储到redis当中
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);//封装进去
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds()); //设置cookie的有效期，和session保持一致
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    //根据token从redis中获取user信息
    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期，把缓冲中的时间更新
        if (miaoshaUser != null)
            addCookie(response, token, miaoshaUser);
        return miaoshaUser;
    }

    public boolean register(RegisterVo registerVo) {
        if (registerVo == null) {
            throw new GlobalException(CodeMsg.REGISTER_EMPTY);
        }
        //检查是否已经注册过了
        String mobile = registerVo.getMobile();
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user != null) {
            throw new GlobalException(CodeMsg.MOBILE_REPEAT);
        }

        //密码二次加密
        MiaoshaUser miaoshaUser = new MiaoshaUser();
        String password = registerVo.getPassword();
        miaoshaUser.setSalt("1l0o2v2e");
        //对输入密码的MD5码进行第二次MD5加密
        String doubleMD5Pass = Md5Util.formToDbPass(password, "1l0o2v2e");
        miaoshaUser.setPassword(doubleMD5Pass);
        miaoshaUser.setId(Long.parseLong(mobile));
        miaoshaUser.setNickname(registerVo.getNickname());
        miaoshaUser.setLoginCount(0);
        miaoshaUser.setRegisterDate(new Date());
        miaoshaUserDao.insertUser(miaoshaUser);
        return true;
    }


}
