package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.ExportExcelUtil;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Field;
import java.util.*;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser> implements UserService {


    private TbUserMapper userMapper;

    @Autowired
    public UserServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper = userMapper;
    }


    @Override
    public void exportUserData() {

        try {
            //查询所有用户信息
            List<TbUser> list = userMapper.selectAll();

            //创建头部信息(每一列的头部信息就是一个map)
            List<Map<String, Object>> headInfoList = new ArrayList<Map<String, Object>>();
            Map<String, Object> itemMap = new HashMap<String, Object>();

            TbUser user = new TbUser();
            Class<? extends TbUser> clazz = user.getClass();
            Field[] fields = clazz.getDeclaredFields();
            int totalPointer = 1;//放一个统计指针，统计一行记录有几列
            for (Field field : fields) {
                field.setAccessible(true);//设置字段可见
                itemMap = new HashMap<String, Object>();
                itemMap.put("title",(String) field.getName());//设置列名与字段名相同
                itemMap.put("columnWidth", 25);
                itemMap.put("dataKey", "XH"+totalPointer);
                headInfoList.add(itemMap);

                totalPointer++;
            }


            //数据表格信息
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            Map<String, Object> dataItem = null;
            for (TbUser tbUser : list) {
                dataItem = new HashMap<String, Object>();
                int tag = 1;
                for (Field field : fields) {
                    field.setAccessible(true);
                    dataItem.put("XH"+tag,  field.get(tbUser)+"");
                    tag++;

                }
                dataList.add(dataItem);
            }
            ExportExcelUtil.exportExcel2FilePath("user sheet 1","C:\\Users\\13790\\Desktop\\user.xls", headInfoList, dataList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }


    }



    /**
     * 统计用户总人数
     *
     * @return
     */
    @Override
    public Integer countTotalUsers() {
        TbUser tbUser = new TbUser();
        int total = userMapper.selectCount(tbUser);
        return total;
    }

    @Override
    public void frozenAccount() {

        Date threeMonthDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(threeMonthDate);//把当前时间赋给日历
        calendar.add(Calendar.MONTH, -3);//设置得到三月前时间
        threeMonthDate = calendar.getTime();

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThan("lastLoginTime", threeMonthDate);


        TbUser tbuser = new TbUser();
        tbuser.setStatus("N");
        userMapper.updateByExampleSelective(tbuser, example);

    }

    @Override
    public void updateUserStatus(String status, Long[] ids) {

        for (Long id : ids) {
            TbUser tbUser = new TbUser();
            tbUser.setStatus(status);
            tbUser.setId(id);
            userMapper.updateByPrimaryKeySelective(tbUser);
        }
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


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if (user != null) {
            if (StringUtils.isNotBlank(user.getUsername())) {
                criteria.andLike("username", "%" + user.getUsername() + "%");
                //criteria.andUsernameLike("%"+user.getUsername()+"%");
            }
            if (StringUtils.isNotBlank(user.getPassword())) {
                criteria.andLike("password", "%" + user.getPassword() + "%");
                //criteria.andPasswordLike("%"+user.getPassword()+"%");
            }
            if (StringUtils.isNotBlank(user.getPhone())) {
                criteria.andLike("phone", "%" + user.getPhone() + "%");
                //criteria.andPhoneLike("%"+user.getPhone()+"%");
            }
            if (StringUtils.isNotBlank(user.getEmail())) {
                criteria.andLike("email", "%" + user.getEmail() + "%");
                //criteria.andEmailLike("%"+user.getEmail()+"%");
            }
            if (StringUtils.isNotBlank(user.getSourceType())) {
                criteria.andLike("sourceType", "%" + user.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(user.getNickName())) {
                criteria.andLike("nickName", "%" + user.getNickName() + "%");
                //criteria.andNickNameLike("%"+user.getNickName()+"%");
            }
            if (StringUtils.isNotBlank(user.getName())) {
                criteria.andLike("name", "%" + user.getName() + "%");
                //criteria.andNameLike("%"+user.getName()+"%");
            }
            if (StringUtils.isNotBlank(user.getStatus())) {
                criteria.andLike("status", "%" + user.getStatus() + "%");
                //criteria.andStatusLike("%"+user.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(user.getHeadPic())) {
                criteria.andLike("headPic", "%" + user.getHeadPic() + "%");
                //criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
            }
            if (StringUtils.isNotBlank(user.getQq())) {
                criteria.andLike("qq", "%" + user.getQq() + "%");
                //criteria.andQqLike("%"+user.getQq()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsMobileCheck())) {
                criteria.andLike("isMobileCheck", "%" + user.getIsMobileCheck() + "%");
                //criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsEmailCheck())) {
                criteria.andLike("isEmailCheck", "%" + user.getIsEmailCheck() + "%");
                //criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getSex())) {
                criteria.andLike("sex", "%" + user.getSex() + "%");
                //criteria.andSexLike("%"+user.getSex()+"%");
            }

        }
        List<TbUser> all = userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

}
