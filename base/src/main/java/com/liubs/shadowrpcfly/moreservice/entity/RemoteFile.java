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
     * 分片
     */
    @ShadowField(2)
    private int sharding;

    /**
     * 总数量
     */
    @ShadowField(3)
    private int sum;

    /**
     * 字节流
     */
    @ShadowField(4)
    private byte[] bytes;


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

    public int getSharding() {
        return sharding;
    }

    public void setSharding(int sharding) {
        this.sharding = sharding;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
