package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * @Autor HumgTop
 * @Date 2021/6/10 15:53
 * @Version 1.0
 */
public class TestFile {
    @Test
    public void testChunk() throws FileNotFoundException {
        File sourceFile = new File("D:\\JavaProject\\xcEdu\\学成在线-网课\\13-在线学习 HLS\\资料\\lucene.avi");
        String chunkFileFolder = "D:\\JavaProject\\xcEdu\\video\\chunks";
        //定义块文件size
        long chunkFileSize = 1024 * 1024;
        //块数
        long chunkFileNum = (long) Math.ceil(sourceFile.length() / (double) chunkFileSize);
        //创建读文件的对象
        RandomAccessFile raf_read=new RandomAccessFile(sourceFile,"r");
    }
}
