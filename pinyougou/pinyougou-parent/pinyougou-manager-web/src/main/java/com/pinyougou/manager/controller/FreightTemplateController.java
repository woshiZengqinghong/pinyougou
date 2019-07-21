package com.pinyougou.manager.controller;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbFreightTemplate;
import com.pinyougou.sellergoods.service.FreightTemplateService;

import com.github.pagehelper.PageInfo;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/freightTemplate")
public class FreightTemplateController {

	@Reference
	private FreightTemplateService freightTemplateService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbFreightTemplate> findAll(){			
		return freightTemplateService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbFreightTemplate> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return freightTemplateService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param freightTemplate
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbFreightTemplate freightTemplate){
		try {
			freightTemplateService.add(freightTemplate);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param freightTemplate
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbFreightTemplate freightTemplate){
		try {
			freightTemplateService.update(freightTemplate);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbFreightTemplate findOne(@PathVariable(value = "id") Long id){
		return freightTemplateService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			freightTemplateService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbFreightTemplate> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbFreightTemplate freightTemplate) {
        return freightTemplateService.findPage(pageNo, pageSize, freightTemplate);
    }
	
}
