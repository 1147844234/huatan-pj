package com.tanhua.server.test;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FastDfsTest {

    // 注入fastdfs文件上传客户端对象
    @Autowired
    private FastFileStorageClient storageClient;
    // 注入web服务器对象，用户获取服务器地址
    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Test
    public void upload() throws IOException {
        String path = "F:\\1.jpg";
        File file = new File(path);
        // 通过客户端对象，进行文件上传，返回上传的文件信息
        StorePath storePath = storageClient.uploadFile(Files.newInputStream(file.toPath()), file.length(), "jpg", null);
        // 获取文件地址信息
        String fullPath = storePath.getFullPath();
        System.out.println("fullPath = " + fullPath);

        // 获取服务端地址、拼接完整的url地址
        String url = fdfsWebServer.getWebServerUrl() + fullPath;
        System.out.println("url = " + url);

    }
}
