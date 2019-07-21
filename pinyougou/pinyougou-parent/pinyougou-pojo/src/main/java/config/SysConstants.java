package config;

public class SysConstants {
    //秒杀中的某商品的对列名前缀
    //一个商品就是一个队列 队列中的数据便是商品本身 队列的长度便是商品的库存
    public static final String SEC_KILL_GOODS_PREFIX="SEC_KILL_GOODS_ID_";

    //用于表示抢购的下单的排队
    public static final String SEC_KILL_USER_ORDER_LIST="SEC_KILL_USER_ORDER_LIST";

    //用于标识某个商品被抢购的人数队列的名字前缀 一个商品就是一个队列
    public static final String SEC_KILL_LIMIT_PREFIX="SEC_KILL_LIMIT_SEC_ID_";

    //用于标识用户已秒杀下单排队中的key
    public static final String SEC_USER_QUEUE_FLAG_KEY="SEC_USER_QUEUE_FLAG_KEY";


    public static final String CONTENT_REDIS_KEY = "CONTENT_REDIS_KEY";

    //所有商品的集合数据的key
    public static final String SEC_KILL_GOODS = "seckillGoods";


    //秒杀商品的订单的key
    public static final String SEC_KILL_ORDER="seckillOrder";

}
