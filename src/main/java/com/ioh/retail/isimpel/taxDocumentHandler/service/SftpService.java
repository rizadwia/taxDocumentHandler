package com.ioh.retail.isimpel.taxDocumentHandler.service;

import com.ioh.retail.isimpel.taxDocumentHandler.model.SftpConnectionFactory;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;

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
    private final SftpConnectionFactory sftpConnectionFactory;

    public SftpService(ObjectPool<ChannelSftp> sftpPool, SftpConnectionFactory sftpConnectionFactory) {
        this.sftpPool = sftpPool;
        this.sftpConnectionFactory = sftpConnectionFactory;
    }

    public List<String> listFiles(String directory) throws Exception {
        ChannelSftp sftp = null;
        try {
            sftp = sftpPool.borrowObject();
            if (!validateConnection(sftp)) {
                sftp = reconnect(sftp);
            }
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
            returnObjectToPool(sftp);
        }
    }

    public void uploadFile(String directory, String filename, InputStream inputStream) throws Exception {
        ChannelSftp sftp = null;
        try {
            sftp = sftpPool.borrowObject();
            if (!validateConnection(sftp)) {
                sftp = reconnect(sftp);
            }
            sftp.put(inputStream, directory + "/" + filename);
        } catch (Exception e) {
            logger.error("Error uploading file: {}/{}", directory, filename, e);
            throw e;
        } finally {
            returnObjectToPool(sftp);
        }
    }

    public InputStream downloadFile(String directory, String filename) throws Exception {
        ChannelSftp sftp = null;
        try {
            sftp = sftpPool.borrowObject();
            if (!validateConnection(sftp)) {
                sftp = reconnect(sftp);
            }
            return sftp.get(directory + "/" + filename);
        } catch (Exception e) {
            logger.error("Error downloading file: {}/{}", directory, filename, e);
            throw e;
        } finally {
            returnObjectToPool(sftp);
        }
    }

    private boolean validateConnection(ChannelSftp sftp) throws JSchException {
        return sftp.isConnected() && sftp.getSession().isConnected();
    }

    private ChannelSftp reconnect(ChannelSftp sftp) throws Exception {
        return sftpConnectionFactory.reconnect(sftp);
    }

    private void returnObjectToPool(ChannelSftp sftp) {
        if (sftp != null) {
            try {
                sftpPool.returnObject(sftp);
            } catch (Exception e) {
                logger.error("Failed to return SFTP connection to pool", e);
                try {
                    sftpPool.invalidateObject(sftp);
                } catch (Exception ex) {
                    logger.error("Failed to invalidate SFTP connection", ex);
                }
            }
        }
    }
}
