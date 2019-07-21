package com.pinyougou.upload.controller;

import com.pinyougou.utils.FastDFSClient;
import entity.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public class UploadController {

    @RequestMapping("/uploadFile")
    //支持跨域 只有这个两个的跨域请求上传图片才可以被允许
    @CrossOrigin(origins = {"http://localhost:9102","http://localhost:9101"},allowCredentials = "true")
    public Result upload(@RequestParam(value = "file") MultipartFile file){

        try {

            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String path = fastDFSClient.uploadFile(bytes, extName);// group1/M00/00/05/wKgZhVx_dy-ABPVLAANdC6JX9KA933.jpg
            String realPath="http://192.168.25.133/"+path;
            return new Result(true,realPath);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
