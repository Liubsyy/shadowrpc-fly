package com.liubs.shadowrpcfly.server.moreservice.upload;

import com.liubs.shadowrpcfly.constant.ResultCode;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.moreservice.entity.RemoteFile;
import com.liubs.shadowrpcfly.moreservice.upload.IUploadService;
import com.liubs.shadowrpcfly.protocol.Result;
import com.liubs.shadowrpcfly.server.annotation.ShadowService;
import com.liubs.shadowrpcfly.util.FileMD5Calculator;
import io.netty.util.internal.StringUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liubsyy
 * @date 2024/8/17
 **/
@ShadowService(serviceName=IUploadService.URL)
public class UploadService implements IUploadService {
    private static final Logger logger = Logger.getLogger(UploadService.class);

    private static boolean uploadFile(String path, byte[] content) {
        try (FileOutputStream fos = new FileOutputStream(path)){
            fos.write(content);
            return true;
        } catch (IOException e) {
            logger.error("写入文件时发生错误",e);
        }
        return false;
    }

    @Override
    public Result uploadSharding(RemoteFile remoteFile) {
        Result result = new Result();
        if(StringUtil.isNullOrEmpty(remoteFile.getFilePath())
                || remoteFile.getSharding()<=0
                || remoteFile.getSum()<=0
                || remoteFile.getBytes() == null) {
            result.setCode(ResultCode.PARAM_MISSING);
            result.setMessage("参数缺失");
            return result;
        }

        String filePath = remoteFile.getFilePath().replace("\\", "/");
        File dir = new File(filePath.substring(0,filePath.lastIndexOf("/")));
        if(!dir.exists()) {
            dir.mkdirs();   //创建目录
        }

        if(remoteFile.getSum() > 1) {
            filePath += "_"+remoteFile.getSharding();
        }

        if(!uploadFile(filePath,remoteFile.getBytes())){
            result.setCode(ResultCode.FAIL);
            result.setMessage("上传失败");
            return result;
        }

        logger.info("Upload success,file={}",filePath);
        return result;
    }

    @Override
    public Result mergeSharding(String filePath,String md5) {
        filePath = filePath.replace("\\", "/");
        int lastIndexOf = filePath.lastIndexOf("/");
        if(lastIndexOf<0 || lastIndexOf>=filePath.length()-1) {
            return Result.fail("没有找到目录");
        }
        File dir = new File(filePath.substring(0,lastIndexOf));
        if(!dir.exists()){
            return Result.fail("没有找到目录"+dir.getPath());
        }

        Result result = new Result();

        // 获取父路径
        Path path = Paths.get(filePath);
        String parentDir = path.getParent().toString();
        String fileName = path.getFileName().toString();

        // 获取分割文件的前缀（不包括最后的"_1", "_2"等）
        String filePrefix = fileName + "_";

        List<File> subFiles = new ArrayList<>();

        // 创建输出流用于合并后的文件
        try (BufferedOutputStream mergedFile = new BufferedOutputStream(Files.newOutputStream(path))) {
            for (int partNumber = 1; ; partNumber++) {
                // 构建当前分割文件的路径
                String partFileName = filePrefix + partNumber;
                File partFile = new File(parentDir, partFileName);

                // 如果分割文件不存在，则结束循环
                if (!partFile.exists()) {
                    break;
                }

                // 读取分割文件并写入合并文件
                try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(partFile.toPath()))) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        mergedFile.write(buffer, 0, bytesRead);
                    }
                }
                subFiles.add(partFile);
            }

            if(subFiles.isEmpty()) {
                result.setCode(ResultCode.FAIL);
                result.setMessage("未找到需要合并的子文件");
            }

            logger.info("Merge success,file={}",filePath);
        }catch (Exception e) {
            logger.error("mergeSharding err:"+filePath,e);
            result.setCode(ResultCode.FAIL);
        }

        String calMd5 = FileMD5Calculator.calculateMD5(new File(filePath));
        if(!md5.equals(calMd5)) {
            result.setCode(ResultCode.FAIL);
            result.setMessage("md5校验不成功");
            return result;
        }

        //删除子文件
        subFiles.forEach(File::delete);
        return result;
    }

}
