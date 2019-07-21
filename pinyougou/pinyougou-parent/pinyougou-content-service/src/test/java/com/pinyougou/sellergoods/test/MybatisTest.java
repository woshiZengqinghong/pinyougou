package com.pinyougou.sellergoods.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class MybatisTest {

    @Autowired
    private TbBrandMapper brandMapper;

//    @Test
//    public void testSelect(){
//        List<TbBrand> select = brandMapper.select(null);
//
//        System.out.println(select);
//
//    }

    @Test
    public void testInsert(){
        TbBrand tbBrand = new TbBrand();
        tbBrand.setFirstChar("F");
        tbBrand.setName("FFFF");
        brandMapper.insert(tbBrand);

        TbBrand tbBrand1 = new TbBrand();
//        tbBrand1.setFirstChar("G");
        tbBrand1.setName("GGGG");
        brandMapper.insertSelective(tbBrand1);//表示非空的才插入
    }

    @Test
    public void testDelete(){
        TbBrand tbBrand = new TbBrand();
        tbBrand.setId(34L);
        brandMapper.delete(tbBrand);//根据条件来删除
        brandMapper.deleteByPrimaryKey(33L);//根据主键来删除
        System.out.println("==========");

        //根据条件来删除
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        List<Long> ids = new ArrayList<>();
        ids.add(37L);
        criteria.andIn("id",ids);

        brandMapper.deleteByExample(example);

    }

    @Test
    public void testUpdate(){
        TbBrand tbBrand = new TbBrand();
        tbBrand.setId(32L);
        tbBrand.setName("NBA");
//        brandMapper.updateByPrimaryKey(tbBrand);

        brandMapper.updateByPrimaryKeySelective(tbBrand);
    }

    @Test
    public void testSelect(){
        brandMapper.selectByPrimaryKey(36L);

        //根据条件查询 如果参数example空，则查询所有
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        List<Long> ids = new ArrayList<>();
        ids.add(36L);
        criteria.andIn("id",ids);
        brandMapper.selectByExample(example);

        //根据等号条件查询
        TbBrand tbBrand = new TbBrand();
        tbBrand.setName("呵呵");
        tbBrand.setFirstChar("H");
        brandMapper.select(tbBrand);
    }

    @Test
    public void testPage(){
        //当前页  size 每页显示多少条
        int page = 1,size=10;
        //分页处理，只需要调用PageHelper.startPage静态方法
        PageHelper.startPage(page,size);

        //查询
        List<TbBrand> brands = brandMapper.selectAll();

        //获取分页信息，注意这里传入了brands集合
        PageInfo<TbBrand> tbBrandPageInfo = new PageInfo<>(brands);
        System.out.println(tbBrandPageInfo);
    }
}
