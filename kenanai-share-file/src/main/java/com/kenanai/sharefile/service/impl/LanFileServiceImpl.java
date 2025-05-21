package com.kenanai.sharefile.service.impl;

import com.kenanai.sharefile.model.LanFile;
import com.kenanai.sharefile.service.LanFileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LanFileServiceImpl implements LanFileService {
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, LanFile> fileStore = new ConcurrentHashMap<>();
    private final Map<String, Set<Integer>> chunkMap = new ConcurrentHashMap<>();

    @Value("${sharefile.upload-dir:uploads}")
    private String uploadDir;
    @Value("${sharefile.chunk-dir:chunks}")
    private String chunkDir;

    @Override
    public LanFile uploadFile(MultipartFile file, String senderId, String receiverId) {
        try {
            String fileType = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileId = UUID.randomUUID().toString();
            String fileName = fileId + "." + fileType;
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path filePath = dir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            LanFile lanFile = new LanFile();
            lanFile.setFileId(fileId);
            lanFile.setFileName(file.getOriginalFilename());
            lanFile.setFileSize(file.getSize());
            lanFile.setFileType(fileType);
            lanFile.setSenderId(senderId);
            lanFile.setReceiverId(receiverId);
            lanFile.setUrl("/api/lan/files/download/" + fileId);
            fileStore.put(fileId, lanFile);

            // WebSocket推送文件消息
            messagingTemplate.convertAndSendToUser(
                    receiverId,
                    "/queue/messages",
                    lanFile
            );

            return lanFile;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public void uploadChunk(MultipartFile file, String fileId, int chunkIndex) {
        try {
            Path dir = Paths.get(chunkDir, fileId);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path chunkPath = dir.resolve(String.valueOf(chunkIndex));
            Files.copy(file.getInputStream(), chunkPath);
            chunkMap.computeIfAbsent(fileId, k -> new HashSet<>()).add(chunkIndex);
        } catch (IOException e) {
            throw new RuntimeException("分片上传失败", e);
        }
    }

    @Override
    public LanFile mergeChunks(String fileId, int totalChunks, String fileName, String senderId, String receiverId) {
        try {
            Path chunkFolder = Paths.get(chunkDir, fileId);
            String fileType = FilenameUtils.getExtension(fileName);
            Path targetDir = Paths.get(uploadDir);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path targetFile = targetDir.resolve(fileId + "." + fileType);
            try (RandomAccessFile raf = new RandomAccessFile(targetFile.toFile(), "rw")) {
                for (int i = 0; i < totalChunks; i++) {
                    Path chunkPath = chunkFolder.resolve(String.valueOf(i));
                    byte[] bytes = Files.readAllBytes(chunkPath);
                    raf.write(bytes);
                }
            }
            // 删除分片
            for (int i = 0; i < totalChunks; i++) {
                Files.deleteIfExists(chunkFolder.resolve(String.valueOf(i)));
            }
            Files.deleteIfExists(chunkFolder);
            chunkMap.remove(fileId);

            LanFile lanFile = new LanFile();
            lanFile.setFileId(fileId);
            lanFile.setFileName(fileName);
            lanFile.setFileSize(Files.size(targetFile));
            lanFile.setFileType(fileType);
            lanFile.setSenderId(senderId);
            lanFile.setReceiverId(receiverId);
            lanFile.setUrl("/api/lan/files/download/" + fileId);
            fileStore.put(fileId, lanFile);

            messagingTemplate.convertAndSendToUser(
                    receiverId,
                    "/queue/messages",
                    lanFile
            );

            return lanFile;
        } catch (IOException e) {
            throw new RuntimeException("分片合并失败", e);
        }
    }

    @Override
    public List<Integer> getUploadedChunks(String fileId) {
        return chunkMap.getOrDefault(fileId, Collections.emptySet())
                .stream().sorted().collect(Collectors.toList());
    }

    @Override
    public LanFile getFile(String fileId) {
        return fileStore.get(fileId);
    }
} 