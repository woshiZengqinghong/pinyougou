import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package PACKAGE_NAME *
 * @since 1.0
 */
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class MybatisGuanfangTest {

   /* @Autowired
    private TbBrandMapper brandMapper;

    //增
    @Test
    public void insert(){
        TbBrand brand = new TbBrand();
        brand.setName("黑马");
        brand.setFirstChar("H");
        //如果不为空就插入数据
        //brandMapper.insertSelective()
        brandMapper.insert(brand);
    }

    //删

    @Test
    public void delete(){

        brandMapper.deleteByPrimaryKey(38L);
    }

    //改
    @Test
    public void update(){

        //update tb_brand set id=?,,... where id=1
        TbBrand brand = new TbBrand();//更新后的对象
        brand.setId(37L);
        brand.setName("黄马");
        // 有值 就更新 没值 就赋空
        brandMapper.updateByPrimaryKey(brand);
        //brandMapper.up
        // 有值 就更新 没有值 不更新
        //brandMapper.updateByPrimaryKeySelective()
    }


    //查询
    @Test
    public void select(){
        //查询所有的数据  eXMAPLE 就是表示的 wehre条件

        //selectByExample  就类似于 ： select * from tb_brand
        //Example  就是 where条件

        //select * from tb_brand where name = '黄马'

        TbBrandExample exmaple = new TbBrandExample();
        TbBrandExample.Criteria criteria = exmaple.createCriteria();

        criteria.andNameEqualTo("黄马");//name = '黄马'

        List<TbBrand> brands = brandMapper.selectByExample(exmaple);

        System.out.println(brands);
    }*/

}
