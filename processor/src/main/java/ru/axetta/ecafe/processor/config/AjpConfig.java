package ru.axetta.ecafe.processor.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AjpConfig {
    @Value("${ajp.port}")
    int ajpPort;

    @Value("${server.http.port}")
    int httpPort;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return server -> {
            if (server instanceof TomcatServletWebServerFactory) {
                ((TomcatServletWebServerFactory) server).addAdditionalTomcatConnectors(redirectConnector(), httpConnector());
            }
        };
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("AJP/1.3");
        connector.setScheme("http");
        connector.setPort(ajpPort);
        connector.setSecure(false);
        connector.setAllowTrace(false);
        return connector;
    }

    private Connector httpConnector() {
        Connector connector = new Connector("HTTP/1.1");
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setAllowTrace(false);
        return connector;
    }

}
