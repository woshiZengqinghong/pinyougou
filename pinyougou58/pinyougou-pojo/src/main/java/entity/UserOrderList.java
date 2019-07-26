package entity;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/*
  Created by IntelliJ IDEA.
  User: EvanLI
  Date: 2019/7/24 0024
  Time: 10:42
*/
public class UserOrderList implements Serializable {

    private String sellerName;//商家名称
    private TbOrder order;
    private List<TbOrderItem> orderItemList;//购物车明细

    @Override
    public String toString() {
        return "UserOrderList{" +
                "sellerName='" + sellerName + '\'' +
                ", order=" + order +
                ", orderItemList=" + orderItemList +
                '}';
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public TbOrder getOrder() {
        return order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
