package com.ioh.retail.isimpel.taxDocumentHandler.model;

import com.jcraft.jsch.*;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SftpConnectionFactory extends BasePooledObjectFactory<ChannelSftp> {
    private static SftpConnectionFactory instance;
    private String host;
    private int port;
    private String username;
    private String password;
    private static final Logger logger = LoggerFactory.getLogger(SftpConnectionFactory.class);

    private SftpConnectionFactory(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public static synchronized SftpConnectionFactory getInstance(String host, int port, String username, String password) {
        if (instance == null) {
            instance = new SftpConnectionFactory(host, port, username, password);
        }
        return instance;
    }

    private ChannelSftp createConnection() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(this.username, this.host, this.port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        return (ChannelSftp) channel;
    }

    @Override
    public ChannelSftp create() throws Exception {
        return createConnection();
    }

    public ChannelSftp reconnect(ChannelSftp sftp) throws JSchException {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
            if (sftp.getSession().isConnected()) {
                sftp.getSession().disconnect();
            }
        }
        return createConnection();
    }

    @Override
    public PooledObject<ChannelSftp> wrap(ChannelSftp channelSftp) {
        return new DefaultPooledObject<>(channelSftp);
    }

    @Override
    public void destroyObject(PooledObject<ChannelSftp> p) throws Exception {
        ChannelSftp sftp = p.getObject();
        sftp.exit();
        sftp.getSession().disconnect();
    }

    @Override
    public boolean validateObject(PooledObject<ChannelSftp> p) {
        ChannelSftp sftp = p.getObject();
        try {
            if(!(sftp.isConnected() && sftp.getSession().isConnected())) reconnect(sftp);
            
            return true;
        } catch (JSchException ex) {
            logger.error("Error validating connection status: ", ex);
            return false;
        }
    }
}
