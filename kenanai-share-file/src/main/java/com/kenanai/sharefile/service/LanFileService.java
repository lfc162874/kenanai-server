package com.kenanai.sharefile.service;

import com.kenanai.sharefile.model.LanFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LanFileService {
    /**
     * 上传文件
     */
    LanFile uploadFile(MultipartFile file, String senderId, String receiverId);

    /**
     * 下载文件
     */
    LanFile getFile(String fileId);

    // 分片上传相关
    void uploadChunk(MultipartFile file, String fileId, int chunkIndex);
    LanFile mergeChunks(String fileId, int totalChunks, String fileName, String senderId, String receiverId);
    List<Integer> getUploadedChunks(String fileId);
} 