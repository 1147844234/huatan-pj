package com.tanhua.server.test;

import com.tanhua.commons.template.AipFaceTemplate;
import com.tanhua.commons.template.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AipFaceTest {

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    @Test
    public void oss() throws Exception {
        String path = "F:\\2.jpg";
        File file = new File(path);
        boolean detect = aipFaceTemplate.detect(Files.readAllBytes(file.toPath()));
        System.out.println("是否人脸：" + detect);
    }

}
