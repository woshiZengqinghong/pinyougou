package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.content.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl extends CoreServiceImpl<TbGoods> implements GoodsService {


    private TbGoodsMapper goodsMapper;

    @Autowired
    public GoodsServiceImpl(TbGoodsMapper goodsMapper) {
        super(goodsMapper, TbGoods.class);
        this.goodsMapper = goodsMapper;
    }


    @Autowired
    private TbGoodsDescMapper GoodsDescMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbSellerMapper tbSellerMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public void delete(Object[] ids) {
        Example example = new Example(TbGoods.class);

        Long[] ides = new Long[ids.length];

        for (int i = 0; i < ids.length; i++) {
            ides[i] = (Long) ids[i];
        }
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ides));

        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete(true);
        goodsMapper.updateByExampleSelective(tbGoods,example);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        TbGoods tbGoods = new TbGoods();
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);
    }

    @Override
    public void update(Goods goods) {
        TbGoods tbGoods = goods.getTbGoods();
        tbGoods.setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(tbGoods);
        //更新描述
        GoodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
        //更新SKU 先删除原来的SPUid对应的SKU的列表
        TbItem tbItem = new TbItem();
        tbItem.setGoodsId(tbGoods.getId());
        tbItemMapper.delete(tbItem);
        //新增就可以了 这里也要判断是否为启用的状态
        saveItems(goods,tbGoods,goods.getTbGoodsDesc());
    }

    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc tbGoodsDesc = GoodsDescMapper.selectByPrimaryKey(id);
        System.out.println(tbGoods.getCategory3Id()+"daaaa");

        TbItem tbItem = new TbItem();
        tbItem.setGoodsId(id);
        List<TbItem> tbItemList = tbItemMapper.select(tbItem);
        goods.setTbGoods(tbGoods);
        goods.setTbGoodsDesc(tbGoodsDesc);
        goods.setItemList(tbItemList);
        return goods;
    }

    @Override
    public void add(Goods goods) {
        //获取goods
        TbGoods tbGoods = goods.getTbGoods();
        tbGoods.setAuditStatus("0");
        tbGoods.setIsDelete(false);
        goodsMapper.insert(tbGoods);
        //获取goodsDesc
        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
        tbGoodsDesc.setGoodsId(tbGoods.getId());
        GoodsDescMapper.insert(tbGoodsDesc);

        saveItems(goods, tbGoods, tbGoodsDesc);
    }

    private void saveItems(Goods goods, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        if ("1".equals(tbGoods.getIsEnableSpec())) {

            //先获取SKU的列表
            List<TbItem> itemList = goods.getItemList();

            for (TbItem tbItem : itemList) {

                //设置title SPU名 + 空格 + 规格名称
                String spec = tbItem.getSpec();
                String title = tbGoods.getGoodsName();
                Map map = JSON.parseObject(spec, Map.class);//{"网络":"移动4G","机身内存":"16G"}
                for (Object key : map.keySet()) {
                    String o1 = (String) map.get(key);
                    title += " " + o1;
                }
                tbItem.setTitle(title);

                //设置图片从tbGoodsDesc中获取
                //[{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
                String itemImages = tbGoodsDesc.getItemImages();

                List<Map> maps = JSON.parseArray(itemImages, Map.class);
                String url = maps.get(0).get("url").toString();//图片的地址
                tbItem.setImage(url);

                //设置分类
                TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
                tbItem.setCategoryid(tbItemCat.getId());
                tbItem.setCategory(tbItemCat.getName());

                //设置时间
                tbItem.setCreateTime(new Date());
                tbItem.setUpdateTime(new Date());

                //设置SPU的ID
                tbItem.setGoodsId(tbGoods.getId());

                //设置商家
                TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
                tbItem.setSellerId(tbSeller.getSellerId());
                tbItem.setSeller(tbSeller.getNickName());//店铺名

                //设置品牌名后
                TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
                tbItem.setBrand(tbBrand.getName());

                tbItemMapper.insert(tbItem);

            }
        } else {
            //插入到SKU表的一条记录
            TbItem tbItem = new TbItem();
            tbItem.setTitle(tbGoods.getGoodsName());
            tbItem.setPrice(tbGoods.getPrice());
            tbItem.setNum(999);//默认一个
            tbItem.setStatus("1");//正常启用
            tbItem.setIsDefault("1");//默认的

            tbItem.setSpec("{}");

            //设置图片从goodsDesc中获取
            //[{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
            String itemImages = tbGoodsDesc.getItemImages();//
            List<Map> maps = JSON.parseArray(itemImages, Map.class);
            String url = maps.get(0).get("url").toString();//图片的地址
            tbItem.setImage(url);

            //设置分类
            TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            tbItem.setCategoryid(tbItemCat.getId());
            tbItem.setCategory(tbItemCat.getName());

            //时间
            tbItem.setCreateTime(new Date());
            tbItem.setUpdateTime(new Date());

            //设置SPU的ID
            tbItem.setGoodsId(tbGoods.getId());

            //设置品牌名后
            TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
            tbItem.setBrand(tbBrand.getName());
            tbItemMapper.insert(tbItem);
        }

    }


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbGoods> all = goodsMapper.selectAll();
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods goods) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("isDelete",false);//只查询没有删除的
        if (StringUtils.isNotBlank(goods.getSellerId())) {
            criteria.andEqualTo("sellerId", goods.getSellerId());
        }

        if (goods != null) {
            if (StringUtils.isNotBlank(goods.getSellerId())) {
                criteria.andEqualTo("sellerId",  goods.getSellerId());
                //criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(goods.getGoodsName())) {
                criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
                //criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if (StringUtils.isNotBlank(goods.getAuditStatus())) {
                criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
                //criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsMarketable())) {
                criteria.andLike("isMarketable", "%" + goods.getIsMarketable() + "%");
                //criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
            }
            if (StringUtils.isNotBlank(goods.getCaption())) {
                criteria.andLike("caption", "%" + goods.getCaption() + "%");
                //criteria.andCaptionLike("%"+goods.getCaption()+"%");
            }
            if (StringUtils.isNotBlank(goods.getSmallPic())) {
                criteria.andLike("smallPic", "%" + goods.getSmallPic() + "%");
                //criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsEnableSpec())) {
                criteria.andLike("isEnableSpec", "%" + goods.getIsEnableSpec() + "%");
                //criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
            }

        }
        List<TbGoods> all = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

}
