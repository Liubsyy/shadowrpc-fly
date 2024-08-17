package com.liubs.shadowrpcfly.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author Liubsyy
 * @date 2024/8/17
 **/

public class FileMD5Calculator {

    public static String calculateMD5(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            // 创建 MD5 摘要计算实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 创建缓冲区用于读取文件内容
            byte[] buffer = new byte[1024];
            int bytesRead;

            // 逐块读取文件内容并更新摘要
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            // 获取最终的 MD5 摘要字节数组
            byte[] md5Bytes = md.digest();

            // 将字节数组转换为十六进制格式的字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : md5Bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
