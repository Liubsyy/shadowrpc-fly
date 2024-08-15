package rpctest.upload;

import com.liubs.shadowrpcfly.server.annotation.ShadowService;
import com.liubs.shadowrpcfly.config.ServerConfig;
import com.liubs.shadowrpcfly.logging.Logger;
import com.liubs.shadowrpcfly.server.init.ServerBuilder;
import org.junit.Test;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Liubsyy
 * @date 2024/1/14
 **/
@ShadowService(serviceName = "uploadService")
public class UploadServiceImpl implements IUploadService{

    private static final Logger logger = Logger.getLogger(UploadServiceImpl.class);


    @Override
    public boolean upload(String name, byte[] bytes) {
        try {
            File uploadDir = new File("target/upload/");
            if(!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            Path path = Paths.get("target/upload/"+name);
            logger.info("upload file {},size={}",path.toFile().getName(),bytes.length);
            Files.write(path,bytes,
                    StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Test
    public void startServer(){
        ServerBuilder.newBuilder()
                .serverConfig(new ServerConfig())
                .addPackage("rpctest.upload")
                .build()
                .start()
                .keep();

    }
}
