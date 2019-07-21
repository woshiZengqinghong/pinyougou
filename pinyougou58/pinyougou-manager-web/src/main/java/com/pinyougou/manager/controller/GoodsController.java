package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.Result;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	@Reference
	private ItemSearchService itemSearchService;

	@Reference
	private ItemPageService itemPageService;

	@Autowired
	private DefaultMQProducer defaultMQProducer;
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
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取到商家的ID
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(sellerId);
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

	/***
	 * 修改状态
	 * @param status
	 * @param ids
	 * @return
	 */
	@RequestMapping("/updateStatus/{status}")
	public Result updateStatus(@PathVariable("status") String status, @RequestBody Long[] ids){
		try {
			//修改数据库
			goodsService.updateStatus(status,ids);
			//添加索引
			if ("1".equals(status)) {
				//通过spu goodsId查询sku
				/*List<TbItem> tbItemList = goodsService.findTbItemListByIds(ids);
				//加入es服务器数据
				itemSearchService.updateIndex(tbItemList);*/

				//创建sku静态文件
				/*for (Long id : ids) {
					itemPageService.genItemHtml(id);
				}*/

				//获取存入索引对象
				List<TbItem> tbItemList = goodsService.findTbItemListByIds(ids);
				//设置message-- topic tag key body methods
				MessageInfo messageInfo = new MessageInfo(tbItemList,"goodsUpdate",
						"goods_update_tag","updateStatus",MessageInfo.METHOD_UPDATE);

				Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),
						messageInfo.getKeys(),JSON.toJSONString(messageInfo).getBytes());

				//发送消息
				defaultMQProducer.setSendMsgTimeout(10000);
				SendResult send = defaultMQProducer.send(message);

				System.out.println("JSON.toJSONString(messageInfo)"+JSON.toJSONString(messageInfo));
				System.out.println(send.getSendStatus());
			}
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
			//删除数据库
			goodsService.delete(ids);
			//删除es服务器索引
			//itemSearchService.deleteByIds(ids);

			//消息队列
			//设置message
			MessageInfo messageInfo = new MessageInfo(ids,"Goods_Topic","goods_delete_tag",
															"delete",MessageInfo.METHOD_DELETE);

			Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),
					messageInfo.getKeys(),JSON.toJSONString(messageInfo).getBytes());
			//发送message
			defaultMQProducer.send(message);
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
		goods.setAuditStatus("0");
		return goodsService.findPage(pageNo, pageSize, goods);
    }

	
}
