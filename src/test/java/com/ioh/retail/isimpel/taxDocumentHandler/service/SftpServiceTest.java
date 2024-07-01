package com.ioh.retail.isimpel.taxDocumentHandler.service;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.pool2.ObjectPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SftpServiceTest {
    private SftpService sftpService;
    private ObjectPool<ChannelSftp> sftpPool;
    private ChannelSftp channelSftp;

    @BeforeEach
    public void setUp() {
        sftpPool = mock(ObjectPool.class);
        channelSftp = mock(ChannelSftp.class);
        sftpService = new SftpService(sftpPool);
    }
    
    @Test
    public void testListFiles() throws Exception {
        when(sftpPool.borrowObject()).thenReturn(channelSftp);
        when(channelSftp.ls("/directory")).thenReturn((Vector<ChannelSftp.LsEntry>) new Vector<ChannelSftp.LsEntry>(Arrays.asList(
            mock(ChannelSftp.LsEntry.class),
            mock(ChannelSftp.LsEntry.class)
        )));
        List<String> files = sftpService.listFiles("/directory");
        assertNotNull(files);
        verify(sftpPool).returnObject(channelSftp);
    }
    
    @Test
    public void testUploadFile() throws Exception {
        when(sftpPool.borrowObject()).thenReturn(channelSftp);
        InputStream inputStream = new ByteArrayInputStream("file content".getBytes());
        sftpService.uploadFile("/directory", "file.txt", inputStream);
        verify(channelSftp).put(inputStream, "/directory/file.txt");
        verify(sftpPool).returnObject(channelSftp);
    }

    @Test
    public void testDownloadFile() throws Exception {
        when(sftpPool.borrowObject()).thenReturn(channelSftp);
        InputStream inputStream = new ByteArrayInputStream("file content".getBytes());
        when(channelSftp.get("/directory/file.txt")).thenReturn(inputStream);
        InputStream result = sftpService.downloadFile("/directory", "file.txt");
        assertNotNull(result);
        verify(sftpPool).returnObject(channelSftp);
    }
}
