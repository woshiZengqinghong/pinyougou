package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.content.service.SellerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SellerServiceImpl extends CoreServiceImpl<TbSeller>  implements SellerService {

	
	private TbSellerMapper sellerMapper;

	@Autowired
	public SellerServiceImpl(TbSellerMapper sellerMapper) {
		super(sellerMapper, TbSeller.class);
		this.sellerMapper=sellerMapper;
	}



	@Override
	public void add(TbSeller seller) {
		seller.setStatus("0");
		seller.setCreateTime(new Date());
		sellerMapper.insert(seller);
	}

	@Override
	public void updateStatus(String sellerId, String status) {
		TbSeller seller = new TbSeller();
		seller.setStatus(status);
		seller.setSellerId(sellerId);

		//根据主键来修改,只修改status字段
		sellerMapper.updateByPrimaryKeySelective(seller);
	}

	@Override
    public PageInfo<TbSeller> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSeller> all = sellerMapper.selectAll();
        PageInfo<TbSeller> info = new PageInfo<TbSeller>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeller> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSeller> findPage(Integer pageNo, Integer pageSize, TbSeller seller) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSeller.class);
        Example.Criteria criteria = example.createCriteria();

        if(seller!=null){			
						if(StringUtils.isNotBlank(seller.getSellerId())){
				criteria.andLike("sellerId","%"+seller.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+seller.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(seller.getName())){
				criteria.andLike("name","%"+seller.getName()+"%");
				//criteria.andNameLike("%"+seller.getName()+"%");
			}
			if(StringUtils.isNotBlank(seller.getNickName())){
				criteria.andLike("nickName","%"+seller.getNickName()+"%");
				//criteria.andNickNameLike("%"+seller.getNickName()+"%");
			}
			if(StringUtils.isNotBlank(seller.getPassword())){
				criteria.andLike("password","%"+seller.getPassword()+"%");
				//criteria.andPasswordLike("%"+seller.getPassword()+"%");
			}
			if(StringUtils.isNotBlank(seller.getEmail())){
				criteria.andLike("email","%"+seller.getEmail()+"%");
				//criteria.andEmailLike("%"+seller.getEmail()+"%");
			}
			if(StringUtils.isNotBlank(seller.getMobile())){
				criteria.andLike("mobile","%"+seller.getMobile()+"%");
				//criteria.andMobileLike("%"+seller.getMobile()+"%");
			}
			if(StringUtils.isNotBlank(seller.getTelephone())){
				criteria.andLike("telephone","%"+seller.getTelephone()+"%");
				//criteria.andTelephoneLike("%"+seller.getTelephone()+"%");
			}
			if(StringUtils.isNotBlank(seller.getStatus())){
				criteria.andLike("status","%"+seller.getStatus()+"%");
				//criteria.andStatusLike("%"+seller.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(seller.getAddressDetail())){
				criteria.andLike("addressDetail","%"+seller.getAddressDetail()+"%");
				//criteria.andAddressDetailLike("%"+seller.getAddressDetail()+"%");
			}
			if(StringUtils.isNotBlank(seller.getLinkmanName())){
				criteria.andLike("linkmanName","%"+seller.getLinkmanName()+"%");
				//criteria.andLinkmanNameLike("%"+seller.getLinkmanName()+"%");
			}
			if(StringUtils.isNotBlank(seller.getLinkmanQq())){
				criteria.andLike("linkmanQq","%"+seller.getLinkmanQq()+"%");
				//criteria.andLinkmanQqLike("%"+seller.getLinkmanQq()+"%");
			}
			if(StringUtils.isNotBlank(seller.getLinkmanMobile())){
				criteria.andLike("linkmanMobile","%"+seller.getLinkmanMobile()+"%");
				//criteria.andLinkmanMobileLike("%"+seller.getLinkmanMobile()+"%");
			}
			if(StringUtils.isNotBlank(seller.getLinkmanEmail())){
				criteria.andLike("linkmanEmail","%"+seller.getLinkmanEmail()+"%");
				//criteria.andLinkmanEmailLike("%"+seller.getLinkmanEmail()+"%");
			}
			if(StringUtils.isNotBlank(seller.getLicenseNumber())){
				criteria.andLike("licenseNumber","%"+seller.getLicenseNumber()+"%");
				//criteria.andLicenseNumberLike("%"+seller.getLicenseNumber()+"%");
			}
			if(StringUtils.isNotBlank(seller.getTaxNumber())){
				criteria.andLike("taxNumber","%"+seller.getTaxNumber()+"%");
				//criteria.andTaxNumberLike("%"+seller.getTaxNumber()+"%");
			}
			if(StringUtils.isNotBlank(seller.getOrgNumber())){
				criteria.andLike("orgNumber","%"+seller.getOrgNumber()+"%");
				//criteria.andOrgNumberLike("%"+seller.getOrgNumber()+"%");
			}
			if(StringUtils.isNotBlank(seller.getLogoPic())){
				criteria.andLike("logoPic","%"+seller.getLogoPic()+"%");
				//criteria.andLogoPicLike("%"+seller.getLogoPic()+"%");
			}
			if(StringUtils.isNotBlank(seller.getBrief())){
				criteria.andLike("brief","%"+seller.getBrief()+"%");
				//criteria.andBriefLike("%"+seller.getBrief()+"%");
			}
			if(StringUtils.isNotBlank(seller.getLegalPerson())){
				criteria.andLike("legalPerson","%"+seller.getLegalPerson()+"%");
				//criteria.andLegalPersonLike("%"+seller.getLegalPerson()+"%");
			}
			if(StringUtils.isNotBlank(seller.getLegalPersonCardId())){
				criteria.andLike("legalPersonCardId","%"+seller.getLegalPersonCardId()+"%");
				//criteria.andLegalPersonCardIdLike("%"+seller.getLegalPersonCardId()+"%");
			}
			if(StringUtils.isNotBlank(seller.getBankUser())){
				criteria.andLike("bankUser","%"+seller.getBankUser()+"%");
				//criteria.andBankUserLike("%"+seller.getBankUser()+"%");
			}
			if(StringUtils.isNotBlank(seller.getBankName())){
				criteria.andLike("bankName","%"+seller.getBankName()+"%");
				//criteria.andBankNameLike("%"+seller.getBankName()+"%");
			}
	
		}
        List<TbSeller> all = sellerMapper.selectByExample(example);
        PageInfo<TbSeller> info = new PageInfo<TbSeller>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeller> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
