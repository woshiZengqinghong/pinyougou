import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

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
public class MybatisCommonMapperTest {
    @Autowired
    private TbBrandMapper brandMapper;
    @Test
    public void insert(){
        TbBrand brand = new TbBrand();
        brand.setName("黑马");
        brand.setFirstChar("H");
        //如果不为空就插入数据
        //brandMapper.insertSelective()
        brandMapper.insert(brand);
        System.out.println(brand.getId());


    }

    @Test
    public void select(){

        TbBrand tbbrand = new TbBrand();//此时 它 是条件
        tbbrand.setName("黑马");
       // List<TbBrand> select = brandMapper.select(tbbrand);//select * from tb_bradn where name="黑马"


        Example exmaple = new Example(TbBrand.class);//条件指定的时候查询 tb_brand的表

        Example.Criteria criteria = exmaple.createCriteria();
        criteria.andEqualTo("name","黑马");// where  name = "黑马"
        criteria.andGreaterThan("id",40L);//   and  id >40
        List<TbBrand> select = brandMapper.selectByExample(exmaple);
        System.out.println(select);
    }

    @Test
    public void findALL(){
        List<TbBrand> all = brandMapper.findAll();
        System.out.println(all);
    }
}
