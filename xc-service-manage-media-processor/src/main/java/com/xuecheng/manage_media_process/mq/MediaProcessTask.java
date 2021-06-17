package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Autor HumgTop
 * @Date 2021/6/12 14:51
 * @Version 1.0
 */
@Component
public class MediaProcessTask {
    @Autowired
    MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;

    @Value("${xc-service-manage-media.video-location}")
    String videoRootPath;


    //消费者：接收视频处理消息进行视频处理
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",
            containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg) {
        //1.解析消息内容
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");

        //2.使用mediaId从数据库查询文件信息（此时原始avi视频文件已上传至服务器）
        Optional<MediaFile> optionalMediaFile = mediaFileRepository.findById(mediaId);
        if (!optionalMediaFile.isPresent()) {
            return;
        }
        MediaFile mediaFile = optionalMediaFile.get();
        String fileType = mediaFile.getFileType();
        if (!fileType.equals("avi")) {
            mediaFile.setProcessStatus("303004");   //无需处理
            mediaFileRepository.save(mediaFile);
            return;
        } else {
            mediaFile.setProcessStatus("303001");   //处理中
            mediaFileRepository.save(mediaFile);
        }

        //3.使用工具类将avi文件转换成mp4
        String filePath = mediaFile.getFilePath();
        String video_path = videoRootPath + filePath;    //视频文件的存放路径
        String mp4FolderPath = mediaFile.getFileFolderPath();
        String mp4FileName = mediaFile.getFileId() + ".mp4";

        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,
                video_path,
                mp4FileName,
                mp4FolderPath);

        String result = mp4VideoUtil.generateMp4();

        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        if (result == null || !result.equals("success")) {
            //处理失败
            mediaFile.setProcessStatus("303003");
            //定义mediaFileProcess_m3u8，记录处理失败信息

            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }

        //4.将mp4生成m3u8和ts文件
        String mp4FilePath = mp4FolderPath + mp4FileName;   //mp4文件路径
        String m3u8FileName = mediaFile.getFileId() + ".m3u8";
        //m308文件目录
        String m3u8FolderPath = mp4FolderPath + "hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path, mp4FilePath, m3u8FileName, m3u8FolderPath);
        //生成m3u8和ts文件
        String tsResult = hlsVideoUtil.generateM3u8();
        if (tsResult == null || !tsResult.equals("success")) {
            //处理失败
            mediaFile.setProcessStatus("303003");
            //定义mediaFileProcess_m3u8，记录处理失败信息
            mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //处理成功
        mediaFile.setProcessStatus("303002");
        //记录ts文件列表到mongoDB中
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        //保存fileUrl
        String fileUrl = m3u8FolderPath + "/" + mediaFile.getFileId() + ".m3u8";
        mediaFile.setFileUrl(fileUrl);
        mediaFileRepository.save(mediaFile);
    }
}
