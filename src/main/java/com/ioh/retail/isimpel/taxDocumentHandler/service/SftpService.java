package com.ioh.retail.isimpel.taxDocumentHandler.service;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SftpService {
    private static final Logger logger = LoggerFactory.getLogger(SftpService.class);
    private final ObjectPool<ChannelSftp> sftpPool;

    public SftpService(ObjectPool<ChannelSftp> sftpPool) {
        this.sftpPool = sftpPool;
    }

    public List<String> listFiles(String directory) throws Exception {
        ChannelSftp sftp = null;
        try {
            sftp = sftpPool.borrowObject();
            @SuppressWarnings("unchecked")
            Vector<ChannelSftp.LsEntry> entries = sftp.ls(directory);
            Stream<ChannelSftp.LsEntry> stream = entries.stream();
            Stream<String> filenameStream = stream.map(entry -> entry.getFilename());
            List<String> filenames = filenameStream.collect(Collectors.toList());

            return filenames;
        } catch (Exception e) {
            logger.error("Error listing files in directory: {}", directory, e);
            throw e;
        } finally {
            if (sftp != null) {
                sftpPool.returnObject(sftp);
            }
        }
    }

    public void uploadFile(String directory, String filename, InputStream inputStream) throws Exception {
        ChannelSftp sftp = null;
        try {
            sftp = sftpPool.borrowObject();
            sftp.put(inputStream, directory + "/" + filename);
        } catch (Exception e) {
            logger.error("Error uploading file: {}/{}", directory, filename, e);
            throw e;
        } finally {
            if (sftp != null) {
                sftpPool.returnObject(sftp);
            }
        }
    }

    public InputStream downloadFile(String directory, String filename) throws Exception {
        ChannelSftp sftp = null;
        try {
            sftp = sftpPool.borrowObject();
            return sftp.get(directory + "/" + filename);
        } catch (Exception e) {
            logger.error("Error downloading file: {}/{}", directory, filename, e);
            throw e;
        } finally {
            if (sftp != null) {
                sftpPool.returnObject(sftp);
            }
        }
    }
}
