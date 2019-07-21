package com.pinyougou.upload.controller;

import com.pinyougou.common.util.FastDFSClient;
import entity.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.upload.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/upload")
public class UploadFileController {

    /**
     * 请求 uploadFile
     * 参数：文件本身
     * 返回值：result    { success:true,String message="URL"}
     */
    @RequestMapping("/uploadFile")
    //支持跨域
    @CrossOrigin(origins = {"http://localhost:18088","http://localhost:18089"},allowCredentials = "true")
    public Result uploadFile(MultipartFile file){
        try {
            //1.获取文件的字节数组
            byte[] bytes = file.getBytes();
            //2.获取原文件的扩展名  不要点
            String fileName = file.getOriginalFilename();//  1235.jpg

            String extName = fileName.substring(fileName.lastIndexOf(".")+1);//  .jpg
            //3.创建fastdfs的配置文件

            //4. 最核心的代码 上传图片
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");
            //
            String file_id= fastDFSClient.uploadFile(bytes, extName); //     group1/M00/00/05/wKgZhVzaOmmABd6qAAClQrJOYvs167.jpg

            //拼接 url
            String realUrl= "http://192.168.25.133/"+file_id;

            return new Result(true,realUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
