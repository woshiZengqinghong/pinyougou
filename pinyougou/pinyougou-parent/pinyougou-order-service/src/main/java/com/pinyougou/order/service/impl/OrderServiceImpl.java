package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.utils.IdWorker;
import entity.Cart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder> implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;


    @Autowired
    private IdWorker idWorker;


    private TbOrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(TbOrderMapper orderMapper) {
        super(orderMapper, TbOrder.class);
        this.orderMapper = orderMapper;
    }

    /**
     * 增加
     *
     * @param order
     */
    @Override
    public void add(TbOrder order) {
        //得到购物车的数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        List<Long> orderList = new ArrayList<>();
        double total_money = 0;//总金额(元)
        for (Cart cart : cartList) {
            long orderId = idWorker.nextId();
            System.out.println("sellerId:" + cart.getSellerId());
            TbOrder tbOrder = new TbOrder();//创建新的订单对象
            tbOrder.setOrderId(orderId);//订单ID
            tbOrder.setUserId(order.getUserId());//用户名
            tbOrder.setPaymentType(order.getPaymentType());//支付方式
            tbOrder.setStatus("1");//状态：未付款
            tbOrder.setCreateTime(new Date());//订单创建日期
            tbOrder.setUpdateTime(new Date());//订单更新日期
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收获地点名称
            tbOrder.setReceiverMobile(order.getReceiverMobile());//收获电话
            tbOrder.setReceiver(order.getReceiver());//收货人
            tbOrder.setSourceType(order.getSourceType());//订单来源
            tbOrder.setSellerId(cart.getSellerId());//商家ID
            //循环购物车明细
            double money = 0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);//订单ID
                orderItem.setSellerId(cart.getSellerId());//商家用户名
                TbItem item = itemMapper.selectByPrimaryKey(orderItem.getItemId());//设置商家id
                orderItem.setGoodsId(item.getGoodsId());//设置商品的SPU的ID

                money += orderItem.getTotalFee().doubleValue();//金额累计
                orderItemMapper.insert(orderItem);

            }
            tbOrder.setPayment(new BigDecimal(money));

            total_money+=money;
            orderMapper.insert(tbOrder);
        }
        TbPayLog payLog = new TbPayLog();
        String outTradeNo=  idWorker.nextId()+"";//支付订单号
        payLog.setOutTradeNo(outTradeNo);//支付订单号
        payLog.setCreateTime(new Date());//创建时间
        //订单号列表，逗号分隔
        String ids = orderList.toString().replace("[", "").replace("]", "").replace("", "");
        payLog.setOrderList(ids);//订单号列表，逗号分隔
        payLog.setPayType("1");//支付类型
        payLog.setTotalFee((long) (total_money*100));//总金额(分)
        payLog.setTradeState("0");//支付状态
        payLog.setUserId(order.getUserId());//用户id
        payLogMapper.insert(payLog);//插入到支付日志表
        redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);//存入到redis中

        //删除redis中的购物车的数据
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //修改支付日志状态
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());
        payLog.setTradeState("1");//已支付
        payLog.setTransactionId(transaction_id);//交易号
        payLogMapper.updateByPrimaryKey(payLog);

        //2.修改dingdanzhuangtai
        String orderList = payLog.getOrderList();//获取订单号列表
        String[] orderIds = orderList.split(",");//获取订单数组

        for (String orderId : orderIds) {
            TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            if (order != null) {
                order.setStatus("2");//已经付款
                orderMapper.updateByPrimaryKey(order);
            }
        }
        //清除redis数据
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbOrder> all = orderMapper.selectAll();
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder order) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (order != null) {
            if (StringUtils.isNotBlank(order.getPaymentType())) {
                criteria.andLike("paymentType", "%" + order.getPaymentType() + "%");
                //criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
            }
            if (StringUtils.isNotBlank(order.getPostFee())) {
                criteria.andLike("postFee", "%" + order.getPostFee() + "%");
                //criteria.andPostFeeLike("%"+order.getPostFee()+"%");
            }
            if (StringUtils.isNotBlank(order.getStatus())) {
                criteria.andLike("status", "%" + order.getStatus() + "%");
                //criteria.andStatusLike("%"+order.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(order.getShippingName())) {
                criteria.andLike("shippingName", "%" + order.getShippingName() + "%");
                //criteria.andShippingNameLike("%"+order.getShippingName()+"%");
            }
            if (StringUtils.isNotBlank(order.getShippingCode())) {
                criteria.andLike("shippingCode", "%" + order.getShippingCode() + "%");
                //criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
            }
            if (StringUtils.isNotBlank(order.getUserId())) {
                criteria.andLike("userId", "%" + order.getUserId() + "%");
                //criteria.andUserIdLike("%"+order.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(order.getBuyerMessage())) {
                criteria.andLike("buyerMessage", "%" + order.getBuyerMessage() + "%");
                //criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
            }
            if (StringUtils.isNotBlank(order.getBuyerNick())) {
                criteria.andLike("buyerNick", "%" + order.getBuyerNick() + "%");
                //criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
            }
            if (StringUtils.isNotBlank(order.getBuyerRate())) {
                criteria.andLike("buyerRate", "%" + order.getBuyerRate() + "%");
                //criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiverAreaName())) {
                criteria.andLike("receiverAreaName", "%" + order.getReceiverAreaName() + "%");
                //criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiverMobile())) {
                criteria.andLike("receiverMobile", "%" + order.getReceiverMobile() + "%");
                //criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiverZipCode())) {
                criteria.andLike("receiverZipCode", "%" + order.getReceiverZipCode() + "%");
                //criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiver())) {
                criteria.andLike("receiver", "%" + order.getReceiver() + "%");
                //criteria.andReceiverLike("%"+order.getReceiver()+"%");
            }
            if (StringUtils.isNotBlank(order.getInvoiceType())) {
                criteria.andLike("invoiceType", "%" + order.getInvoiceType() + "%");
                //criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
            }
            if (StringUtils.isNotBlank(order.getSourceType())) {
                criteria.andLike("sourceType", "%" + order.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(order.getSellerId())) {
                criteria.andLike("sellerId", "%" + order.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+order.getSellerId()+"%");
            }

        }
        List<TbOrder> all = orderMapper.selectByExample(example);
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

}
