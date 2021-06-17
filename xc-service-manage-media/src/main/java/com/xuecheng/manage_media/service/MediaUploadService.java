package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @Autor HumgTop
 * @Date 2021/6/11 9:04
 * @Version 1.0
 */
@Service
public class MediaUploadService {
    @Autowired
    MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.upload-location}")
    String uploadLocation;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    String routingKey;

    //文件上传前的注册，检查文件是否存在
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //检查文件在磁盘上是否存在
        String fileFolderPath = getFileFolderPath(fileMd5);
        String filePath = getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        boolean exists = file.exists();
        //检查文件信息在MongoDB中是否存在
        Optional<MediaFile> mediaFileOptional = mediaFileRepository.findById(fileMd5);
        //文件已存在，且MongoDB中有记录
        if (exists && mediaFileOptional.isPresent()) {
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //检查文件所在目录是否存在
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //得到文件所属目录路径
    private String getFileFolderPath(String fileMd5) {
        return uploadLocation + fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/";
    }

    //得到文件的路径
    private String getFilePath(String fileMd5, String fileExt) {
        //文件目录路径
        return getFileFolderPath(fileMd5) + fileMd5 + "." + fileExt;
    }

    //得到块文件所属目录
    private String getChunkFileFolderPath(String fildMd5) {
        return getFileFolderPath(fildMd5) + "/chunk/";
    }

    //分块检查
    public CheckChunkResult checkchunk(String fileMd5, int chunk, int chunkSize) {
        //检查分块是否存在
        //得到分块文件的目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFile = new File(chunkFileFolderPath + chunk);
        if (chunkFile.exists()) {
            return new CheckChunkResult(CommonCode.SUCCESS, true);
        } else {
            return new CheckChunkResult(CommonCode.SUCCESS, false);
        }

    }

    //上传分块
    public ResponseResult uploadchunk(MultipartFile file, String fileMd5, int chunk) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        String chunkFilePath = chunkFileFolderPath + chunk;
        //如果不存在则要自动创建
        if (!chunkFileFolder.exists()) {
            chunkFileFolder.mkdirs();
        }
        //得到上传文件的输入流
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(new File(chunkFilePath));
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //合并分块
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //1.合并分块
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        File[] files = chunkFileFolder.listFiles();
        //创建合并文件
        String mergeFilePath = getFilePath(fileMd5, fileExt);
        File mergeFile = new File(mergeFilePath);
        try {
            mergeFile = mergerFile(Arrays.asList(files), mergeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!mergeFile.exists()) {
            //合并文件失败
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        //2.校验md5
        if (!checkMd5(mergeFile, fileMd5)) {
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //3.向MongoDB写入文件信息
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileOriginalName(fileName);
        mediaFile.setFileName(fileMd5 + "." + fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件保存的相对路径
        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5, fileExt));
        mediaFile.setFileFolderPath(getFileFolderPath(fileMd5));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);

        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        //向mq发送视频处理消息
        sendProcessVideoMsg(mediaFile.getFileId());
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取文件存储的相对路径
    private String getFileFolderRelativePath(String fileMd5, String fileExt) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
    }

    //合并文件
    private File mergerFile(List<File> chunkFileList, File mergeFile) throws Exception {
        if (mergeFile.exists()) {
            mergeFile.delete();
        } else {
            //创建一个新文件
            mergeFile.createNewFile();
        }

        //按照文件名升序排列
        Collections.sort(chunkFileList, Comparator.comparingInt(o -> Integer.parseInt(o.getName())));

        RandomAccessFile randomAccessFile = new RandomAccessFile(mergeFile, "rw");

        byte[] bytes = new byte[1024];
        for (File file : chunkFileList) {
            RandomAccessFile raf_read = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_read.read(bytes)) != -1) {    //读取分块文件到bytes缓冲数组中
                randomAccessFile.write(bytes, 0, len);  //写入到mergeFile中
            }
            raf_read.close();
        }
        randomAccessFile.close();

        return mergeFile;
    }

    //文件md5校验
    private boolean checkMd5(File mergeFile, String md5) {
        try {
            FileInputStream inputStream = new FileInputStream(mergeFile);
            //获取文件的md5值
            String actualMd5 = DigestUtils.md5Hex(inputStream);
            inputStream.close();
            return md5.equalsIgnoreCase(actualMd5);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return true;
        }
    }

    //发送视频处理消息到mq
    public ResponseResult sendProcessVideoMsg(String mediaId) {
        //查询mongoDB是否已有此文件记录
        Optional<MediaFile> optionalMediaFile = mediaFileRepository.findById(mediaId);
        if (!optionalMediaFile.isPresent()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //构造消息内容
        Map<String, String> map = new HashMap<>();
        map.put("mediaId", mediaId);
        String jsonContent = JSON.toJSONString(map);
        //发送消息到交换机
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK, routingKey, jsonContent);
        } catch (AmqpException e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }
}
