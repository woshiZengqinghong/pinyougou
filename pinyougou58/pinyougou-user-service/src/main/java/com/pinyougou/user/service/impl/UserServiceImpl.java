package com.pinyougou.user.service.impl;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbSeller;
import entity.UserOrderList;
import com.pinyougou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser> implements UserService {


    private TbUserMapper userMapper;

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbSellerMapper sellerMapper;


    //短信模板
    @Value("${sign_name}")
    private String signName;

    @Value("${template_code}")
    private String templateCode;

    @Autowired
    public UserServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper = userMapper;
    }


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbUser> all = userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    /*
    * 查询我的订单列表
    * */
    public List<UserOrderList> findOrderList(TbUser user) {

        List<UserOrderList> all = new ArrayList<>();
        //查询该买家的所有订单
        TbOrder tbOrder = new TbOrder();
        if (user != null) {
            if (StringUtils.isNotBlank(user.getUsername())) {
                tbOrder.setUserId(user.getUsername());
            }else {
                System.out.println("user==null?????");
                return null;
            }
        }
        List<TbOrder> tbOrderList = orderMapper.select(tbOrder);
        if (tbOrderList != null && tbOrderList.size()>0) {
            //查询每个订单号对应的所有产品
            for (TbOrder order : tbOrderList) {
                UserOrderList userOrderList = new UserOrderList();
                userOrderList.setOrder(order);

                TbOrderItem tbOrderItem = new TbOrderItem();
                tbOrderItem.setOrderId(order.getOrderId());
                List<TbOrderItem> orderItemList = orderItemMapper.select(tbOrderItem);
                userOrderList.setOrderItemList(orderItemList);
                //查询商家名
                TbSeller seller = sellerMapper.selectByPrimaryKey(order.getSellerId());
                userOrderList.setSellerName(seller.getNickName());
                all.add(userOrderList);
            }
        }
        System.out.println(all);
        return all;
    }

    /***
     * 生成验证码 发送验证码
     * @param phone
     */
    @Override
    public void createSmsCode(String phone) {
        try {
            //生成验证码
            String code = (long) ((Math.random() * 9 + 1) * 100000) + "";
            System.out.println(code);

            //存入redis
            redisTemplate.boundValueOps("Register_" + phone).set(code);

            //发送验证码
            /*Map map = new HashMap<String, String>();
            map.put("mobile", phone);
            map.put("sign_name", signName);
            map.put("template_code", templateCode);
            map.put("param", "{\"code\":\"" + code + "\"}");

            Message message = new Message("SMS_TOPIC", "SEND_MESSAGE_TAG", "createSmsCode", JSON.toJSONString(map).getBytes());
            defaultMQProducer.send(message);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断短信验证码是否存在
     *
     * @param phone
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String code) {
        //根据phone获取redis中的验证码
        String redisCode = (String) redisTemplate.boundValueOps("Register_" + phone).get();
        if (StringUtils.isNotBlank(redisCode) && redisCode.equals(code)) {
            return true;
        }
        return false;
    }

}
