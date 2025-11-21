package com.shopping.common.controller;

import com.shopping.common.annotation.CurrentAdmin;
import com.shopping.common.response.ResponseResult;
import com.shopping.admin.entity.Admin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
public class FileUploadController {
    
    @Value("${file.upload-dir:/app/upload}")
    private String uploadDir;

    @PostMapping("/image")
    public ResponseResult<Map<String, String>> uploadImage(@CurrentAdmin Admin currentAdmin,
                                                             @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseResult.error("文件为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseResult.error("只能上传图片文件");
        }

        // 检查文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseResult.error("文件大小不能超过10MB");
        }

        try {
            // 创建上传目录
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, file.getBytes());

            // 返回文件URL（相对路径）
            String fileUrl = "/upload/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);
            
            return ResponseResult.success("上传成功", result);
        } catch (IOException e) {
            return ResponseResult.error("文件上传失败: " + e.getMessage());
        }
    }
}

