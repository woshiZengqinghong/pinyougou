package com.pinyougou.sellergoods.service.impl;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.common.util.ImportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;  

import com.pinyougou.sellergoods.service.BrandService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class BrandServiceImpl extends CoreServiceImpl<TbBrand>  implements BrandService {

	
	private TbBrandMapper brandMapper;

	@Autowired
	public BrandServiceImpl(TbBrandMapper brandMapper) {
		super(brandMapper, TbBrand.class);
		this.brandMapper=brandMapper;
	}

	
	

	
	@Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbBrand> all = brandMapper.selectAll();
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbBrand> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize, TbBrand brand) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();

        if(brand!=null){			
						if(StringUtils.isNotBlank(brand.getName())){
				criteria.andLike("name","%"+brand.getName()+"%");
				//criteria.andNameLike("%"+brand.getName()+"%");
			}
			if(StringUtils.isNotBlank(brand.getFirstChar())){
				criteria.andLike("firstChar","%"+brand.getFirstChar()+"%");
				//criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
	
		}
        List<TbBrand> all = brandMapper.selectByExample(example);
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbBrand> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public void importBrandList() {
        ImportExcelUtil importExcelUtil=new ImportExcelUtil();
        //excel 导入数据demo
        File file = new File("C:\\Users\\13790\\Desktop\\brand.xlsx");
        List<List<Object>> dataList= null;


        try {
            dataList = importExcelUtil.importExcel(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
//        System.out.println(dataList);
        //数据封装格式一，将表格中的数据遍历取出后封装进对象放进List

        for (int i = 0; i <dataList.size(); i++) {

            TbBrand brand = new TbBrand();

            String id = (String) dataList.get(i).get(0);
            String name = (String) dataList.get(i).get(1);
            String firstChar = (String) dataList.get(i).get(2);

            brand.setId(Long.valueOf(id));
            brand.setName(String.valueOf(name));
            brand.setFirstChar(String.valueOf(firstChar));

            brandMapper.insert(brand);
        }


    }
	
}
