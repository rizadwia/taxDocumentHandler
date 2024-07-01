package com.ioh.retail.isimpel.taxDocumentHandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.ioh.retail.isimpel.taxDocumentHandler.model.MyConfiguration;
import com.ioh.retail.isimpel.taxDocumentHandler.model.SftpConnectionFactory;
import com.ioh.retail.isimpel.taxDocumentHandler.service.SftpService;
import com.jcraft.jsch.ChannelSftp;

@SpringBootApplication
@EnableConfigurationProperties(MyConfiguration.class)
public class TaxDocumentHandlerApplication {
    private static final Logger logger = LoggerFactory.getLogger(SftpService.class);

	public static void main(String[] args) {
		SpringApplication.run(TaxDocumentHandlerApplication.class, args);
	}

	@Bean
    public ObjectPool<ChannelSftp> sftpPool(MyConfiguration config) {
        logger.info("Creating SFTP Connection Factory to sftp://"+ config.getHost() +":"+ config.getPort() +" with username : "+ config.getUsername() +", password : "+ config.getPassword());
        SftpConnectionFactory factory = new SftpConnectionFactory(config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
        GenericObjectPoolConfig<ChannelSftp> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(config.getPoolMax());
        return new GenericObjectPool<>(factory, poolConfig);
    }

}
