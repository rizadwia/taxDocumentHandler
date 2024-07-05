package com.ioh.retail.isimpel.taxDocumentHandler.service;

import com.ioh.retail.isimpel.taxDocumentHandler.model.SftpConnectionFactory;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import org.apache.commons.pool2.ObjectPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SftpServiceTest {
    private SftpService sftpService;
    private ObjectPool<ChannelSftp> sftpPool;
    private ChannelSftp channelSftp;
    private SftpConnectionFactory sftpConnectionFactory;

    @BeforeEach
    public void setUp() {
        sftpPool = mock(ObjectPool.class);
        channelSftp = mock(ChannelSftp.class);
        sftpConnectionFactory = mock(SftpConnectionFactory.class);
        sftpService = new SftpService(sftpPool, sftpConnectionFactory);
    }

    @Test
    public void testListFiles() throws Exception {
        when(sftpPool.borrowObject()).thenReturn(channelSftp);
        when(channelSftp.isConnected()).thenReturn(true);
        when(channelSftp.getSession()).thenReturn(mock(com.jcraft.jsch.Session.class));
        when(channelSftp.getSession().isConnected()).thenReturn(true);
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
        when(channelSftp.isConnected()).thenReturn(true);
        when(channelSftp.getSession()).thenReturn(mock(com.jcraft.jsch.Session.class));
        when(channelSftp.getSession().isConnected()).thenReturn(true);
        InputStream inputStream = new ByteArrayInputStream("file content".getBytes());

        sftpService.uploadFile("/directory", "file.txt", inputStream);
        verify(channelSftp).put(inputStream, "/directory/file.txt");
        verify(sftpPool).returnObject(channelSftp);
    }

    @Test
    public void testDownloadFile() throws Exception {
        when(sftpPool.borrowObject()).thenReturn(channelSftp);
        when(channelSftp.isConnected()).thenReturn(true);
        when(channelSftp.getSession()).thenReturn(mock(com.jcraft.jsch.Session.class));
        when(channelSftp.getSession().isConnected()).thenReturn(true);
        InputStream inputStream = new ByteArrayInputStream("file content".getBytes());
        when(channelSftp.get("/directory/file.txt")).thenReturn(inputStream);

        InputStream result = sftpService.downloadFile("/directory", "file.txt");
        assertNotNull(result);
        verify(sftpPool).returnObject(channelSftp);
    }

    @Test
    public void testListFilesReconnect() throws Exception {
        when(sftpPool.borrowObject()).thenReturn(channelSftp);
        when(channelSftp.isConnected()).thenReturn(false); // Simulate a disconnected state
        when(sftpConnectionFactory.reconnect(channelSftp)).thenReturn(channelSftp);
        when(channelSftp.getSession()).thenReturn(mock(com.jcraft.jsch.Session.class));
        when(channelSftp.getSession().isConnected()).thenReturn(true);
        when(channelSftp.ls("/directory")).thenReturn((Vector<ChannelSftp.LsEntry>) new Vector<ChannelSftp.LsEntry>(Arrays.asList(
            mock(ChannelSftp.LsEntry.class),
            mock(ChannelSftp.LsEntry.class)
        )));

        List<String> files = sftpService.listFiles("/directory");
        assertNotNull(files);
        verify(sftpConnectionFactory).reconnect(channelSftp); // Verify reconnection attempt
        verify(sftpPool).returnObject(channelSftp);
    }

    @Test
    public void testUploadFileReconnect() throws Exception {
        when(sftpPool.borrowObject()).thenReturn(channelSftp);
        when(channelSftp.isConnected()).thenReturn(false); // Simulate a disconnected state
        when(sftpConnectionFactory.reconnect(channelSftp)).thenReturn(channelSftp);
        when(channelSftp.getSession()).thenReturn(mock(com.jcraft.jsch.Session.class));
        when(channelSftp.getSession().isConnected()).thenReturn(true);
        InputStream inputStream = new ByteArrayInputStream("file content".getBytes());

        sftpService.uploadFile("/directory", "file.txt", inputStream);
        verify(channelSftp).put(inputStream, "/directory/file.txt");
        verify(sftpConnectionFactory).reconnect(channelSftp); // Verify reconnection attempt
        verify(sftpPool).returnObject(channelSftp);
    }

    @Test
    public void testDownloadFileReconnect() throws Exception {
        when(sftpPool.borrowObject()).thenReturn(channelSftp);
        when(channelSftp.isConnected()).thenReturn(false); // Simulate a disconnected state
        when(sftpConnectionFactory.reconnect(channelSftp)).thenReturn(channelSftp);
        when(channelSftp.getSession()).thenReturn(mock(com.jcraft.jsch.Session.class));
        when(channelSftp.getSession().isConnected()).thenReturn(true);
        InputStream inputStream = new ByteArrayInputStream("file content".getBytes());
        when(channelSftp.get("/directory/file.txt")).thenReturn(inputStream);

        InputStream result = sftpService.downloadFile("/directory", "file.txt");
        assertNotNull(result);
        verify(sftpConnectionFactory).reconnect(channelSftp); // Verify reconnection attempt
        verify(sftpPool).returnObject(channelSftp);
    }

    @Test
    public void testReturnInvalidObjectToPool() throws Exception {
        when(sftpPool.borrowObject()).thenReturn(channelSftp);
        doThrow(new Exception("Invalid object")).when(sftpPool).returnObject(channelSftp);
        doNothing().when(sftpPool).invalidateObject(channelSftp);
        when(channelSftp.isConnected()).thenReturn(true);
        when(channelSftp.getSession()).thenReturn(mock(com.jcraft.jsch.Session.class));
        when(channelSftp.getSession().isConnected()).thenReturn(true);
        when(channelSftp.ls("/directory")).thenReturn((Vector<ChannelSftp.LsEntry>) new Vector<ChannelSftp.LsEntry>(Arrays.asList(
            mock(ChannelSftp.LsEntry.class),
            mock(ChannelSftp.LsEntry.class)
        )));

        List<String> files = sftpService.listFiles("/directory");
        assertNotNull(files);
        verify(sftpPool).returnObject(channelSftp);
        verify(sftpPool).invalidateObject(channelSftp); // Verify invalidation attempt
    }
}
