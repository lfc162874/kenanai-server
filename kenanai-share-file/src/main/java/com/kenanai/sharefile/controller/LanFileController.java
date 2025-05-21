package com.kenanai.sharefile.controller;

import com.alibaba.nacos.api.model.v2.Result;
import com.kenanai.sharefile.annotation.RequiresLogin;
import com.kenanai.sharefile.model.LanFile;
import com.kenanai.sharefile.service.LanFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/share/lan/files")
@RequiredArgsConstructor
public class LanFileController {
    private final LanFileService lanFileService;

    @RequiresLogin
    @PostMapping("/upload")
    public Result<LanFile> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam("receiverId") String receiverId) {
        String senderId = getCurrentUserId();
        return Result.success(lanFileService.uploadFile(file, senderId, receiverId));
    }

    @RequiresLogin
    @PostMapping("/upload/chunk")
    public Result<Void> uploadChunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileId") String fileId,
                                    @RequestParam("chunkIndex") int chunkIndex) {
        lanFileService.uploadChunk(file, fileId, chunkIndex);
        return Result.success();
    }

    @RequiresLogin
    @PostMapping("/upload/merge")
    public Result<LanFile> mergeChunks(@RequestParam("fileId") String fileId,
                                       @RequestParam("totalChunks") int totalChunks,
                                       @RequestParam("fileName") String fileName,
                                       @RequestParam("receiverId") String receiverId) {
        String senderId = getCurrentUserId();
        return Result.success(lanFileService.mergeChunks(fileId, totalChunks, fileName, senderId, receiverId));
    }

    @RequiresLogin
    @GetMapping("/upload/progress")
    public Result<List<Integer>> getUploadProgress(@RequestParam("fileId") String fileId) {
        return Result.success(lanFileService.getUploadedChunks(fileId));
    }

    @RequiresLogin
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws IOException {
        LanFile lanFile = lanFileService.getFile(fileId);
        if (lanFile == null) {
            return ResponseEntity.notFound().build();
        }
        String fileType = lanFile.getFileType();
        String fileName = fileId + "." + fileType;
        Path filePath = Paths.get("uploads").resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + lanFile.getFileName() + "\"")
                .body(resource);
    }

    private String getCurrentUserId() {
        // TODO: 通过认证体系获取当前用户ID
        return "mockUserId";
    }
} 