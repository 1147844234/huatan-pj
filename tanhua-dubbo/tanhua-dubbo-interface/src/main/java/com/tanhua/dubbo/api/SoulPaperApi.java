package com.tanhua.dubbo.api;

import com.tanhua.domain.db.SoulPaper;

import java.util.zip.ZipFile;

public interface SoulPaperApi {
    SoulPaper selectById(int id);
}
