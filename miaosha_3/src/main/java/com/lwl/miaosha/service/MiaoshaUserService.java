package com.lwl.miaosha.service;

import com.lwl.miaosha.dao.IMiaoshaUserDao;
import com.lwl.miaosha.domain.MiaoshaUser;
import com.lwl.miaosha.exception.GlobalException;
import com.lwl.miaosha.redis.MiaoshaUserKey;
import com.lwl.miaosha.redis.RedisService;
import com.lwl.miaosha.result.CodeMsg;
import com.lwl.miaosha.result.Result;
import com.lwl.miaosha.util.Md5Util;
import com.lwl.miaosha.util.UUIDUtil;
import com.lwl.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-17
 */
//登录校验功能
@Service
public class MiaoshaUserService {
    public static final String COOKIE_NAME_TOKEN="token";

    @Autowired
    RedisService redisService;
    
    @Autowired(required = false)
    IMiaoshaUserDao IMiaoshaUserDao;
    
    public MiaoshaUser getById(long id) {
        return IMiaoshaUserDao.getById(id);
    }

    public Result<Boolean> login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw  new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String inputPassword = loginVo.getPassword();
        //判断手机号在数据库中是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user==null){ //用户不存在
            throw  new GlobalException(CodeMsg.MOBILE_NOTEXIST);
        }
        //验证登录密码
        String dbPassword = user.getPassword();
        String dbSalt = user.getSalt();
        //对输入密码的MD5码进行第二次MD5加密
        String doubleMD5Pass = Md5Util.formToDbPass(inputPassword,dbSalt);
        if(!dbPassword.equals(doubleMD5Pass)){
            throw  new GlobalException(CodeMsg.PASSWORD_ERROR); //密码输入错误
        }
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return Result.success(true);
    }
    //生成cookie
    private void addCookie(HttpServletResponse response,String token,MiaoshaUser user){
        //生成cookie,存储到redis当中
        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);//封装进去
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds()); //设置cookie的有效期，和session保持一致
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    //根据token从redis中获取user信息
    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期，把缓冲中的时间更新
        if(miaoshaUser!=null)
            addCookie(response,token,miaoshaUser);
        return miaoshaUser;
    }
}
