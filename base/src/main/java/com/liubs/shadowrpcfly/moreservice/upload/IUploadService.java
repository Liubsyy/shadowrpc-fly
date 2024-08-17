package com.liubs.shadowrpcfly.moreservice.upload;

import com.liubs.shadowrpcfly.annotation.ShadowInterface;
import com.liubs.shadowrpcfly.moreservice.entity.RemoteFile;
import com.liubs.shadowrpcfly.protocol.Result;

/**
 * 上传文件服务
 * @author Liubsyy
 * @date 2024/8/17
 **/

@ShadowInterface
public interface IUploadService {
    String URL = "uploadService";

    /**
     * 上传文件分片
     * @param remoteFile
     * @return
     */
    Result uploadSharding(RemoteFile remoteFile);

    /**
     * 合并文件
     * @param md5
     * @return
     */
    Result mergeSharding(String filePath,String md5);

}
