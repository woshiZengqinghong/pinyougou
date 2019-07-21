package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import config.SysConstants;
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

    //每30秒执行一次
    @Scheduled(cron = "0/30 * * * * ?")
    public void pushGoods(){
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");
        criteria.andGreaterThan("stockCount",0);
        Date date = new Date();
        criteria.andLessThan("startTime",date);
        criteria.andGreaterThan("endTime",date);

        //排除已经在redis中的商品
        Set<Long> seckillGoodsList = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).keys();

        if (seckillGoodsList!=null && seckillGoodsList.size()>0) {
            criteria.andNotIn("id",seckillGoodsList);
        }

        List<TbSeckillGoods> goods = seckillGoodsMapper.selectByExample(example);
        //全部存储到redis中
        for (TbSeckillGoods good : goods) {
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(good.getGoodsId(),good);

            pushGoodsList(good);
        }
    }

    private void pushGoodsList(TbSeckillGoods goods) {
        //向同一个队列中压入商品数据
        for (int i = 0; i < goods.getStockCount(); i++) {
            //库存为多少就是多少个SIZE，值就是id即可
            redisTemplate.boundListOps(SysConstants.SEC_KILL_LIMIT_PREFIX+goods.getId()).leftPush(goods.getId());
        }
    }
}
