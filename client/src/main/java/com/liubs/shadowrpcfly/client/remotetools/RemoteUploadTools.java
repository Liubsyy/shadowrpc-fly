package com.liubs.shadowrpcfly.client.remotetools;

import com.liubs.shadowrpcfly.client.connection.ShadowClient;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.moreservice.entity.RemoteFile;
import com.liubs.shadowrpcfly.moreservice.upload.IUploadService;
import com.liubs.shadowrpcfly.protocol.Result;
import com.liubs.shadowrpcfly.util.FileMD5Calculator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Liubsyy
 * @date 2024/8/17
 **/
public class RemoteUploadTools {

    private static Logger logger = Logger.getLogger(RemoteUploadTools.class);

    /**
     * 上传文件入口函数
     */
    public static void uploadService(ShadowClient shadowClient, String filePath,String remotePath,int maxSeconds) throws IOException {
        File file = new File(filePath);
        if( !file.exists()) {
            return;
        }
        long fileSize = file.length();

        final int chunkSize = 1024 * 1024;  //1Mb为一次单位
        final int shadingCount = (int)(fileSize % chunkSize == 0 ? fileSize / chunkSize : fileSize / chunkSize+1);

        IUploadService uploadService = shadowClient.createRemoteProxy(IUploadService.class, IUploadService.URL);

        AtomicInteger successCount = new AtomicInteger(0);
        CountDownLatch countDownLatch = new CountDownLatch(shadingCount);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[chunkSize];
            int bytesRead;
            int i = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                // 如果读取的字节数小于缓冲区大小，说明这是最后一块
                i++;
                byte[] bytesClone;
                if (bytesRead < chunkSize) {
                    bytesClone = new byte[bytesRead];
                    System.arraycopy(buffer, 0, bytesClone, 0, bytesRead);
                } else {
                    bytesClone = buffer.clone();
                }

                RemoteFile remoteFile = new RemoteFile();
                remoteFile.setFilePath(remotePath);
                remoteFile.setSharding(i);
                remoteFile.setSum(shadingCount);
                remoteFile.setBytes(bytesClone);

                ShadowClient.<Result>asyncCall(()->{
                    uploadService.uploadSharding(remoteFile);
                },result -> {
                    logger.info("上传文件{},分片{},结果:{}",filePath,remoteFile.getSharding(),result.isSuccess());
                    countDownLatch.countDown();
                    if(result.isSuccess()) {
                        successCount.incrementAndGet();
                    }
                });
            }
        }

        try {
            countDownLatch.await(maxSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("上传超时：{}",filePath);
            return;
        }

        if(successCount.get() < shadingCount) {
            logger.error("上传失败：{}",filePath);
            return;
        }

        if(shadingCount > 1) {
            ShadowClient.<Result>asyncCall(
                    ()-> uploadService.mergeSharding(remotePath, FileMD5Calculator.calculateMD5(file)),
                    mergeResult -> {
                        if(!mergeResult.isSuccess()) {
                            logger.info("合并文件{}失败: {}",filePath,mergeResult.getMessage());
                            return;
                        }
                        logger.info("上传成功: {}",filePath);}
            );

        }else {
            logger.info("上传成功: {}",filePath);
        }

    }
}
