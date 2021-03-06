package com.lwl.miaosha.vo;

import com.lwl.miaosha.domain.MiaoshaUser;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-20
 */
//往页面上传值
public class GoodsDetailVo {
    // 秒杀状态量初始值
    private int miaoshaStatus = 0;
    // 开始时间倒计时
    private int remainSeconds = 0;
    private GoodsVo goods ;
    private MiaoshaUser user;

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }
    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }
    public int getRemainSeconds() {
        return remainSeconds;
    }
    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }
    public GoodsVo getGoods() {
        return goods;
    }
    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }
    public MiaoshaUser getUser() {
        return user;
    }
    public void setUser(MiaoshaUser user) {
        this.user = user;
    }
}
