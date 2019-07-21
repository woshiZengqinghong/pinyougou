package com.pinyougou.manager.controller;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.sellergoods.service.PayLogService;

import com.github.pagehelper.PageInfo;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/payLog")
public class PayLogController {

	@Reference
	private PayLogService payLogService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbPayLog> findAll(){			
		return payLogService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbPayLog> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return payLogService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param payLog
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbPayLog payLog){
		try {
			payLogService.add(payLog);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param payLog
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbPayLog payLog){
		try {
			payLogService.update(payLog);
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
	public TbPayLog findOne(@PathVariable(value = "id") Long id){
		return payLogService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			payLogService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbPayLog> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbPayLog payLog) {
        return payLogService.findPage(pageNo, pageSize, payLog);
    }
	
}
