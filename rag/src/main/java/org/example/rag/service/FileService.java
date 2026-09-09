package org.example.rag.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @author: Azure
 * @date: 2026/9/9 周三 14:58
 * @Version 1.0
 **/
@Service
public class FileService {

    @Autowired
    private TokenTextSplitter tokenTextSplitter;

    @Autowired
    private VectorStore vectorStore;

    //本地文件上传根目录，可在 application.yml 中配置（如 file.upload-dir=./uploads）
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    //解析文件 保存到本地 + 向量库
    public void uploadFile(MultipartFile file) throws IOException {
        // 1. 保存到本地磁盘
        String savedPath = saveToLocal(file);

        // 2. 读取本地文件并解析
        Resource resource = new FileSystemResource(savedPath);
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> read = reader.read();

        // 3. 分词
        List<Document> apply = tokenTextSplitter.apply(read);

        // 4. 向量化入库
        vectorStore.add(apply);
    }

    //将上传的文件落地到本地目录，返回保存后的完整路径
    private String saveToLocal(MultipartFile file) throws IOException {
        // 确保目录存在
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // 用 UUID 前缀避免文件名冲突，保留原始后缀
        String originalName = file.getOriginalFilename();
        String suffix = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String fileName = UUID.randomUUID() + suffix;
        Path target = dir.resolve(fileName);

        file.transferTo(target.toFile());
        return target.toString();
    }
}
