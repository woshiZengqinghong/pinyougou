package com.pinyougou.page.service;

/**
 * 商品详细页接口
 */
public interface ItemPageService {

    /**
     * 生成商品详细页
     * @param goodsId
     */
    public void genItemHtml(Long goodsId);

    void deleteById(Long[] goodsId);
}
