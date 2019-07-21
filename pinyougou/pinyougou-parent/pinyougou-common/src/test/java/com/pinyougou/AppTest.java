package com.pinyougou;

import static org.junit.Assert.assertTrue;

import com.pinyougou.utils.FastDFSClient;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void uploadFastdfsclient() throws Exception{
        FastDFSClient fastDFSClient = new FastDFSClient("D:\\ideaWork\\pinyougou\\pinyougou-parent\\pinyougou-portal-web\\src\\main\\resources\\config\\fdfs_client.conf");
        String jpg = fastDFSClient.uploadFile("C:\\Users\\13790\\Desktop\\u=3005014521,2902045675&fm=27&gp=0.jpg", "jpg");
        System.out.println(jpg);
    }
}
