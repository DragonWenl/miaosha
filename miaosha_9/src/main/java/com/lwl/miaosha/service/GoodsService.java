package com.lwl.miaosha.service;

import com.lwl.miaosha.dao.IGoodsDao;
import com.lwl.miaosha.domain.Goods;
import com.lwl.miaosha.domain.MiaoshaGoods;
import com.lwl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-19
 */
@Service
public class GoodsService {

    @Autowired(required = false)
    IGoodsDao goodsDao;

    //获取商品信息列表
    public List<GoodsVo> getGoodsVoList() {
        return goodsDao.listGoodsVo();
    }
    //获取商品根据商品Id
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    //商品数量减少1
    public boolean reduceStock(GoodsVo goodsVo){
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goodsVo.getId());
        int i = goodsDao.reduceStock(g);
        return i > 0;
    }

    public void resetStock(List<GoodsVo> goodsList) {
        for(GoodsVo goods : goodsList ) {
            MiaoshaGoods g = new MiaoshaGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDao.resetStock(g);
        }
    }
}
