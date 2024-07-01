package com.ioh.retail.isimpel.taxDocumentHandler.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ioh.retail.isimpel.taxDocumentHandler.service.SftpService;

@RestController
@RequestMapping("/sftp")
public class SftpController {
    private static final Logger logger = LoggerFactory.getLogger(SftpController.class);
    private final SftpService sftpService;

    @Autowired
    public SftpController(SftpService sftpService) {
        this.sftpService = sftpService;
    }

    @GetMapping("/list")
    public List<String> listFiles(@RequestParam String directory) {
        try {
            return sftpService.listFiles(directory);
        } catch (Exception e) {
            logger.error("Error listing files in directory: {}", directory, e);
            throw new RuntimeException("Error listing files");
        }
    }

    @PostMapping("/upload")
    public void uploadFile(@RequestParam String directory, @RequestParam String filename, @RequestParam MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            sftpService.uploadFile(directory, filename, inputStream);
        } catch (Exception e) {
            logger.error("Error uploading file: {}/{}", directory, filename, e);
            throw new RuntimeException("Error uploading file");
        }
    }

    @GetMapping("/download")
    public InputStream downloadFile(@RequestParam String directory, @RequestParam String filename) {
        try {
            return sftpService.downloadFile(directory, filename);
        } catch (Exception e) {
            logger.error("Error downloading file: {}/{}", directory, filename, e);
            throw new RuntimeException("Error downloading file");
        }
    }
}
