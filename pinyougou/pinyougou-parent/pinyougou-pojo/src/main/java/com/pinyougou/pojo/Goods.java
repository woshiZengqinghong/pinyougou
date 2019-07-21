package com.pinyougou.pojo;

import java.io.Serializable;
import java.util.List;

public class Goods implements Serializable {
    private TbGoods tbGoods;//商品SPU
    private TbGoodsDesc tbGoodsDesc;//商品扩展
    private List<TbItem> itemList;//商品SKU列表

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getTbGoodsDesc() {
        return tbGoodsDesc;
    }

    public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
        this.tbGoodsDesc = tbGoodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
