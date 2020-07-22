package com.lwl.miaosha.rabbitmq;

import com.lwl.miaosha.domain.MiaoshaUser;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-21
 */
public class MiaoshaMessage {
    private MiaoshaUser miaoshaUser;
    private long goodsId;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
