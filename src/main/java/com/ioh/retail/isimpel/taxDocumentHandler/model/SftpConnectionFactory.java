package com.ioh.retail.isimpel.taxDocumentHandler.model;

import com.jcraft.jsch.*;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class SftpConnectionFactory extends BasePooledObjectFactory<ChannelSftp> {
    private String host;
    private int port;
    private String username;
    private String password;

    public SftpConnectionFactory(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    public ChannelSftp create() throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        return (ChannelSftp) channel;
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
}
