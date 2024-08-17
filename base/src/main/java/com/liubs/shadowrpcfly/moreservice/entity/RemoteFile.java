package com.liubs.shadowrpcfly.moreservice.entity;

import com.liubs.shadowrpcfly.annotation.ShadowEntity;
import com.liubs.shadowrpcfly.annotation.ShadowField;

/**
 * @author Liubsyy
 * @date 2024/8/17
 **/
@ShadowEntity
public class RemoteFile {
    /**
     * 文件路径
     */
    @ShadowField(1)
    private String filePath;

    /**
     * 字节流
     */
    @ShadowField(2)
    private byte[] bytes;

    @ShadowField(3)
    private int response;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
