package com.lwl.miaosha.vo;

import com.lwl.miaosha.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-23
 */
public class RegisterVo {
    @NotNull
    @IsMobile    //自定义一个校验器
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;

    @NotNull
    @Length(min = 3, max = 20)
    private String nickname;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "RegisterVo{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
