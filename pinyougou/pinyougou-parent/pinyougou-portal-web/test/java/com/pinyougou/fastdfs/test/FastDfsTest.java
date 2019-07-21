package com.pinyougou.fastdfs.test;

import com.alibaba.fastjson.JSON;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FastDfsTest {

    @Test
    public void uploadFastdfs() throws Exception {
        ClientGlobal.init("D:\\ideaWork\\pinyougou\\pinyougou-parent\\pinyougou-portal-web\\src\\main\\resources\\config\\fdfs_client.conf");

        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient(trackerServer, null);
        String[] jpgs = storageClient.upload_file("C:\\Users\\13790\\Desktop\\u=3005014521,2902045675&fm=27&gp=0.jpg","jpg",null);
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }

    @Test
    public void testMap(){
        String itemImages = "{'机身内存':'16G','网络':'联通3G'}";

        Map map = JSON.parseObject(itemImages, Map.class);
        System.out.println(map);
    }
}
