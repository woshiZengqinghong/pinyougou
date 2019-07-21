package com.pinyougou.manager.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
//import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.MessageInfo;
import com.pinyougou.pojo.TbItem;
//import com.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import com.github.pagehelper.PageInfo;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

//	@Reference
//	private ItemSearchService itemSearchService;
//
//	@Reference
//	private ItemPageService itemPageService;

	@Autowired
	private DefaultMQProducer producer;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }

    @RequestMapping("/updateStatus/{status}")
    public Result updateStatus(@RequestBody Long[] ids,@PathVariable(value = "status")String  status){
		try {
			goodsService.updateStatus(ids,status);

			if ("1".equals(status)) {
				List<TbItem> tbItemListByIds = goodsService.findTbItemListByIds(ids);
				MessageInfo messageInfo = new MessageInfo("Goods_Topic", "goods_update_tag", "updateStatus", tbItemListByIds, MessageInfo.METHOD_UPDATE);
				SendResult send = producer.send(new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));
				System.out.println(">>>>"+send.getSendStatus());
//				itemSearchService.updateIndex(tbItemListByIds);
//				for (Long id : ids) {
//					itemPageService.genItemHtml(id);
//				}
			}

			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
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
	public Goods findOne(@PathVariable(value = "id") Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			goodsService.delete(ids);
			MessageInfo messageInfo = new MessageInfo("Goods_Topic", "goods_delete_tag", "delete", ids, MessageInfo.METHOD_DELETE);
			producer.send(new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(),JSON.toJSONString(messageInfo).getBytes()));
//			itemSearchService.deleteByIds(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {

		//		goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
//		goods.setSellerId("qiandu");

        return goodsService.findPage(pageNo, pageSize, goods);
    }
	
}
