package com.pinyougou.shop.test;

import com.pinyougou.common.util.FastDFSClient;
import org.csource.fastdfs.*;
import org.junit.Test;

import javax.transaction.xa.XAException;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.test *
 * @since 1.0
 */
public class FastDfsTest {

    //上传图片
    @Test
    public void uploadFile() throws Exception{

        //1.创建一个配置文件 ：配置服务器的ip地址和端口   fastdsf_client.conf

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\ThinkPad\\IdeaProjects\\58pinyougou\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");

        //3.创建trackerclient对象
        TrackerClient trackerClient = new TrackerClient();

        //4.获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建stroageServer   设置null

        StorageServer storageServer = null;

        //6.创建storageClient  使用该client的API 上传文件即可
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        // 参数1  表示文件的路径
        //参数2  表示 文件的扩展名（不要带点）
        //参数3  文件的元数据   file_id
        String[] jpgs = storageClient.upload_file("C:\\Users\\Administrator\\Pictures\\timg.jpg", "jpg", null);

        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }

    @Test
    public void upalodClientTest() throws Exception{
        FastDFSClient fastDFSClient = new FastDFSClient("C:\\Users\\ThinkPad\\IdeaProjects\\58pinyougou\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        String jpg = fastDFSClient.uploadFile("C:\\Users\\Administrator\\Pictures\\timg.jpg", "jpg");
//        String jpg = fastDFSClient.uploadFile();
        System.out.println(jpg);
    }

}
