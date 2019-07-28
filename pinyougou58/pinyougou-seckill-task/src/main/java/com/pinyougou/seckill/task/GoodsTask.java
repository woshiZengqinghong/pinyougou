package com.pinyougou.seckill.task;

import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class GoodsTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 将秒杀商品加入到redis中
     */
    @Scheduled(cron = "*/3 * * * * ?")
    public void pushGoods(){
//        TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("TbSeckillGoods").get(1L);
//        System.out.println(tbSeckillGoods);

        //库存大于0 审核通过的商品
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");
        criteria.andGreaterThan("stockCount","0");
        //排除redis中已有的secKillGoods
        Set<Long> keys = redisTemplate.boundHashOps("TbSeckillGoods").keys();

        //设置存入时间
        if (keys!=null&&keys.size()>0){
            criteria.andNotIn("id",keys);
        }
        Date date = new Date();
        criteria.andGreaterThan("startTime",date);
//        criteria.andGreaterThan("endTime",date);
        System.out.println(">>>>>>>>>>>>>>>>>redis>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        //获取数据
        List<TbSeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
        System.out.println(seckillGoods);

        for (TbSeckillGoods seckillGood : seckillGoods) {
            redisTemplate.boundHashOps("TbSeckillGoods").put(seckillGood.getId(),seckillGood);
            pushGoodsList(seckillGood);
        }
    }

    private void pushGoodsList(TbSeckillGoods goods){
        for (Integer integer = 0; integer < goods.getStockCount(); integer++) {
            redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX+goods.getId()).leftPush(goods.getId());
        }
    }
}
