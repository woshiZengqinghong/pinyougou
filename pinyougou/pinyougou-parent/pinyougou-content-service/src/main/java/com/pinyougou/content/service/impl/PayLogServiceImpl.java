package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.content.service.PayLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class PayLogServiceImpl extends CoreServiceImpl<TbPayLog>  implements PayLogService {

	
	private TbPayLogMapper payLogMapper;

	@Autowired
	public PayLogServiceImpl(TbPayLogMapper payLogMapper) {
		super(payLogMapper, TbPayLog.class);
		this.payLogMapper=payLogMapper;
	}

	
	

	
	@Override
    public PageInfo<TbPayLog> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbPayLog> all = payLogMapper.selectAll();
        PageInfo<TbPayLog> info = new PageInfo<TbPayLog>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbPayLog> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbPayLog> findPage(Integer pageNo, Integer pageSize, TbPayLog payLog) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbPayLog.class);
        Example.Criteria criteria = example.createCriteria();

        if(payLog!=null){			
						if(StringUtils.isNotBlank(payLog.getOutTradeNo())){
				criteria.andLike("outTradeNo","%"+payLog.getOutTradeNo()+"%");
				//criteria.andOutTradeNoLike("%"+payLog.getOutTradeNo()+"%");
			}
			if(StringUtils.isNotBlank(payLog.getUserId())){
				criteria.andLike("userId","%"+payLog.getUserId()+"%");
				//criteria.andUserIdLike("%"+payLog.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(payLog.getTransactionId())){
				criteria.andLike("transactionId","%"+payLog.getTransactionId()+"%");
				//criteria.andTransactionIdLike("%"+payLog.getTransactionId()+"%");
			}
			if(StringUtils.isNotBlank(payLog.getTradeState())){
				criteria.andLike("tradeState","%"+payLog.getTradeState()+"%");
				//criteria.andTradeStateLike("%"+payLog.getTradeState()+"%");
			}
			if(StringUtils.isNotBlank(payLog.getOrderList())){
				criteria.andLike("orderList","%"+payLog.getOrderList()+"%");
				//criteria.andOrderListLike("%"+payLog.getOrderList()+"%");
			}
			if(StringUtils.isNotBlank(payLog.getPayType())){
				criteria.andLike("payType","%"+payLog.getPayType()+"%");
				//criteria.andPayTypeLike("%"+payLog.getPayType()+"%");
			}
	
		}
        List<TbPayLog> all = payLogMapper.selectByExample(example);
        PageInfo<TbPayLog> info = new PageInfo<TbPayLog>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbPayLog> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
