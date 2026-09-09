package org.example.rag.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.rag.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    @Autowired
    FileService fileService;

    @Operation(summary = "上传知识库文件", description = "上传知识库文件并入库向量库")
    @PostMapping("file/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.uploadFile(file);
            return ResponseEntity.ok(Map.of("success", true, "message", "上传成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

}
