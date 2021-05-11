package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Autor HumgTop
 * @Date 2021/5/10 22:10
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {


    //上传文件
    @Test
    public void testUpload() {
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);
            //创建客户端
            TrackerClient tc = new TrackerClient();
            //连接trackerServer
            TrackerServer ts = tc.getConnection();
            if (ts == null) {
                System.out.println("getConnection return null");
                return;
            }
            //获取一个storage server
            StorageServer ss = tc.getStoreStorage(ts);
            if (ss == null) {
                System.out.println("getStoreStorage return null");
            }
            //创建一个storage存储客户端
            StorageClient1 sc1 = new StorageClient1(ts, ss);
//            NameValuePair[] meta_list = null;
            //new NameValuePair[0];
            String item = "D:\\JavaProject\\xcEdu\\Project\\test-fastdfs\\src\\main\\resources\\testUpload2.png";
            String fileid;
            //上传成功后，返回文件id
            fileid = sc1.upload_file1(item, "png", null);
            System.out.println("Upload local file " + item + " ok, fileid=" + fileid);
            //fileid=group1/M00/00/00/CgoKc2CZQ2WAWztwAR-Ig_w-ERs401.jpg
            //group1/M00/00/00/CgoKc2CZ29OALK8fACf3F_-vczU483.png
            //group1/M00/00/00/CgoKc2CZ3MuAJPrMACHwDvIvCgk264.png
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //下载文件
    @Test
    public void testDownloadFile() throws IOException, MyException {
        //加载配置文件
        ClientGlobal.initByProperties("config/fastdfs-client.properties");
        //trackserver客户端
        TrackerClient tracker = new TrackerClient();
        //获取连接
        TrackerServer trackerServer = tracker.getConnection();
        //不知道存储服务器地址，置null
        StorageServer storageServer = null;
        StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
        //根据文件id下载文件
        byte[] result = storageClient1.download_file1("group1/M00/00/00/CgoKc2CZQ2WAWztwAR-Ig_w-ERs401.jpg");
        //输出流到本地
        File file = new File("D:\\JavaProject\\xcEdu\\Project\\test-fastdfs\\src\\main\\resources\\download\\testDown.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(result);
        fileOutputStream.close();
    }
}
