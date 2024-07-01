package com.ioh.retail.isimpel.taxDocumentHandler.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConfigurationProperties(prefix = "sftp")
public class MyConfiguration {

    private String host;
    private int port;
    private String username;
    private String password;
    private int poolMax;
    
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getPoolMax() {
        return poolMax;
    }
    public void setPoolMax(int poolMax) {
        this.poolMax = poolMax;
    }
}

