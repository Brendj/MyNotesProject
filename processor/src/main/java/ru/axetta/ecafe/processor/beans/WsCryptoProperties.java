package ru.axetta.ecafe.processor.beans;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by nuc on 22.10.2020.
 */
@Component
@Qualifier("wsCryptoProperties")
public class WsCryptoProperties extends PropertiesFactoryBean {
    @PostConstruct
    public void init() throws IOException {
        Properties properties = new Properties();
        properties.put("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");
        properties.put("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");
        properties.put("org.apache.ws.security.crypto.merlin.keystore.password", "123456");
        properties.put("org.apache.ws.security.crypto.merlin.file", "/temp/certs/alice.jks");
        properties.put("org.apache.ws.security.crypto.merlin.truststore.type", "PKCS12");
        properties.put("org.apache.ws.security.crypto.merlin.truststore.password", "BCGG00");
        properties.put("org.apache.ws.security.crypto.merlin.truststore.file", "/temp/certs/ispp_agent_istk.pfx");
        this.loadProperties(properties);
    }
}
